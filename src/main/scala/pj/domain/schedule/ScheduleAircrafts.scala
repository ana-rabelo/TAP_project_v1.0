package pj.domain.schedule

import pj.domain.operation.Operation
import pj.domain.agenda.Agenda
import pj.domain.DomainError.*
import pj.domain.SimpleTypes.positiveInteger
import pj.domain.Result
import pj.domain.aircraft.Aircraft
import pj.domain.runway.Runway
import pj.utils.Constraints.*

import scala.collection.immutable.Queue
import scala.xml.Elem

final case class ScheduleAircrafts(operations: List[Operation])

object ScheduleAircrafts:
    
  /**
   * Schedule the aircrafts in the agenda.
   * 
   * @param agenda the agenda with the aircrafts to schedule, the runways and the maximum delay time
   * @return a domain error or the xml wrapped
   */
  def scheduleAgenda(agenda: Agenda): Result[Elem] =
    val aircrafts = agenda.aircrafts.sortBy(_.targetTime)
    val runways = agenda.runways
    val maxDelayTime = positiveInteger.to(agenda.maximumDelayTime)

    allocateRunways(aircrafts, runways, maxDelayTime) match
      case Left(_) => Left(ScheduleError("Error creating schedule."))
      case Right(listOperations) => Operation.operationsToXml(ScheduleAircrafts(listOperations))

  /**
   * Allocate the runways to the aircrafts.
   * 
   * @param aircrafts the list of aircrafts to schedule
   * @param runways the list of runways to schedule
   * @param maxDelayTime the maximum delay time
   * 
   * @return a domain error or the list of operations
   */
  def allocateRunways(aircrafts: List[Aircraft], 
                      runways: List[Runway], 
                      maxDelayTime: Int): Result[List[Operation]] =
                      
    val aircraftQueue: Queue[Aircraft] = Queue(aircrafts*)
    val runwayQueue: Queue[Runway] = Queue(runways*)

    val listOperations = allocateHelper(aircraftQueue, runwayQueue, List.empty[Operation], aircraftQueue.front.targetTime, maxDelayTime, 0L)
    println(listOperations)

    if (listOperations.isEmpty) Left(NoRunwaysAvailable(aircraftQueue.length))
    else (Right(listOperations.reverse))


  /**
   * Get the maximum window time for the aircraft.
   * 
   * @param a the aircraft that can have an emergency
   * @param maxDelayTime the maximum delay time
   * 
   * @return the maximum window time
   * 
   */
  def getMaxWindowTime(a: Aircraft, maxDelayTime: Long): Long =
    if (a.emergency.isDefined)
      a.emergency.fold(ifEmpty = 0)(f = positiveInteger.to(_)) 
    else
      a.targetTime + maxDelayTime


  /**
   * Determine if the runway is available for the aircraft.
   * 
   * @param runway the runway to check
   * @param aircraft the aircraft to check
   * @param operations the list of operations
   * @param currentTime the current time
   *  
   * @return true if the runway is available for the aircraft, false otherwise
   */
  def isRunwayAvailableForAircraft(runway: Runway, aircraft: Aircraft, operations: List[Operation], currentTime: Long): Boolean =
    val runwayOperations = operations.filter(_.runway == runway)
    runwayOperations.forall { operation =>
      val prevAircraft = operation.aircraft
      val timeDiff = calculateSeparation(prevAircraft, aircraft)
      val sepMin = getSepOp(aircraft.classNumber, operation.aircraft.classNumber)((aircraft.classNumber), (operation.aircraft.classNumber))
      val timeRunway = prevAircraft.targetTime
      val cost = prevAircraft.targetTime - currentTime

      (timeDiff >= sepMin) && (currentTime > timeRunway)
    }

  /**
   * Get the list of available runways for the aircraft and their delay.
   * 
   * @param aircrafts the list of aircrafts to schedule
   * @param operations the list of operations
   * @param runways the queue of runways to schedule
   * @param currentTime the current time
   * 
   * @return the list of available runways
   */
  def getAvailableRunwaysForAircraft(aircraft: Aircraft, operations: List[Operation], runways: Queue[Runway], currentTime: Long): List[(Runway, Long)] =
    runways.flatMap { runway =>
      if (isRunwayAvailableForAircraft(runway, aircraft, operations, currentTime))
        val delay = aircraft.targetTime - currentTime
        Some((runway, delay))
      else
        None
    }.toList

  /* private def allocateAircraft(aircraft: Aircraft, availableRunways: List[(Runway, Long)], operations: List[Operation]): Option[List[Operation]] =
    availableRunways.minByOption(_._2).map { case (runway, delay) =>
      val allocation = Operation(aircraft, runway, aircraft.targetTime)
      allocation :: operations
    } */

  /**
   * Allocate aircraft with emergency.
   * 
   * @param aircraft the aircraft to allocate
   * @param operations the list of operations
   * @param runways the queue of runways
   * @param aircrafts the queue of aircrafts
   * @param maxDelayTime the maximum delay time
   * 
   * @return the list of operations
   */
  private def allocateEmergencyAircraft(aircraft: Aircraft, operations: List[Operation], runways: Queue[Runway], aircrafts: Queue[Aircraft], maxDelayTime: Int): List[Operation] =
    val (lastOperation, newOperations) = reverseOperation(operations.reverse)
    val newAircraftQueue = insertFirst(aircrafts, lastOperation.aircraft)
    allocateHelper(insertFirst(newAircraftQueue, aircraft), runways, newOperations, aircraft.targetTime, maxDelayTime, 0L)

  /**
   * Allocate no emergency aircraft.
   * 
   * @param aircraft the aircraft to allocate
   * @param operations the list of operations
   * @param runways the queue of runways
   * @param aircrafts the queue of aircrafts
   * @param maxDelayTime the maximum delay time
   * 
   * @return the list of operations
   */ 
  private def allocateNonEmergencyAircraft(aircraft: Aircraft, operations: List[Operation], runways: Queue[Runway], aircrafts: Queue[Aircraft], maxDelayTime: Int): List[Operation] =
    val currentTargetTime = getSmallSep(aircraft, operations, runways) + aircraft.targetTime
    val delay = currentTargetTime - aircraft.targetTime
    val aircraftDelay = Aircraft(aircraft.id, aircraft.classNumber, currentTargetTime, aircraft.emergency)
    allocateHelper(insertFirst(aircrafts, aircraftDelay), runways, operations, aircraftDelay.targetTime, maxDelayTime, delay)


  /** 
   * Recursive function to allocate the queue of aircrafts.
   * 
   * @param aircrafts the queue of aircrafts to schedule
   * @param runways the queue of runways to schedule
   * @param operations the list of operations
   * @param currentTime the current time
   * @param maxDelayTime the maximum delay time
   * @param cost the cost
   * 
   * @return the list of operations
   */
  def allocateHelper(aircrafts: Queue[Aircraft], 
                    runways: Queue[Runway], 
                    operations: List[Operation], 
                    currentTime: Long, 
                    maxDelayTime: Int,
                    cost: Long): List[Operation] =
                      
    aircrafts.dequeueOption match
      case None => operations

      case Some((newAircraft, remainingAircrafts)) =>
        val maxWindowTime = if (newAircraft.emergency.isDefined) 
                              newAircraft.targetTime + newAircraft.emergency.fold(ifEmpty = 0)(f = positiveInteger.to(_)) 
                              else newAircraft.targetTime + maxDelayTime
        val minWindowTime = newAircraft.targetTime

        if (currentTime >= minWindowTime && currentTime <= maxWindowTime)
          val availableRunways = getAvailableRunwaysForAircraft(newAircraft, operations, runways, currentTime)
          val bestRunway = availableRunways.minByOption(_._2).map(_._1) // find the available runway with the minimum delay
          
          bestRunway match
            case Some(runway) =>
              //val delay = newAircraft.targetTime - currentTime
              val allocation = Operation(newAircraft, runway, newAircraft.targetTime, cost)
              allocateHelper(remainingAircrafts, runways, allocation :: operations, newAircraft.targetTime, maxDelayTime, 0L)

            case None =>
              if(newAircraft.emergency.isDefined)
                allocateEmergencyAircraft(newAircraft, operations, runways, remainingAircrafts, maxDelayTime) 
              else 
                allocateNonEmergencyAircraft(newAircraft, operations, runways, remainingAircrafts, maxDelayTime)
        
        else if (currentTime > maxWindowTime)
          MaximumTimeWindowExceeded(newAircraft.id,maxWindowTime)
          operations
        
        else
          allocateHelper(insertFirst(remainingAircrafts.reverse, newAircraft), runways, operations, newAircraft.targetTime, maxDelayTime, 0L)


