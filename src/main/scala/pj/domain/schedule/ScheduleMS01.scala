package pj.domain.schedule

import scala.xml.Elem
import pj.domain.*
import pj.domain.agenda.Agenda
import pj.domain.aircraft.Aircraft
import pj.domain.runway.Runway
import pj.constraints.SepOp
import pj.domain.schedule.Operation
import pj.domain.aircraft.ClassNumber
import pj.domain.aircraft.Class2
import pj.domain.aircraft.Class3
import pj.domain.aircraft.Class1
import pj.domain.aircraft.Class4
import pj.domain.aircraft.Class5
import pj.domain.aircraft.Class6
import pj.domain.SimpleTypes.positiveInteger
import pj.constraints.SepOp.getSepOp
import scala.collection.immutable.Queue

object ScheduleMS01 extends Schedule:

  // TODO: Create the code to implement a functional domain model for schedule creation
  //       Use the xml.XML code to handle the xml elements
  //       Refer to https://github.com/scala/scala-xml/wiki/XML-Processing for xml creation
  def create(xml: Elem): Result[Elem] = ???

  //is has to be compatible with the runway
  //rhe separation time between two or more aircrafts it has to be less than the separation of operations defined
  //the penalty is for when an aircrfat suffers a delay
  // the feasible window time is between the target time and the maximum dlay time (defined  in the agenda)
  // when the emergency is defined, it substitues the maximum delay time

  private def isAircraftCompatibleWithRunway(aircraft: Aircraft, runway: Runway): Boolean =
    runway.handles.contains(aircraft.classNumber)

  private def calculateSeparation(prevAircraft: Aircraft, aircraft: Aircraft): Long =
    aircraft.targetTime - prevAircraft.targetTime

  /* private def calculatePenalty(operation: ClassNumber, delay: Long): Long =
    if (delay <= 0) 0
    else
      val penalty = operation match
        case Class1 | Class2 | Class3 => 2
        case _ => 1
      penalty * delay */

  def scheduleAgenda(agenda: Agenda) =
    val aircrafts = agenda.aircrafts.sortBy(_.targetTime)
    val runways = agenda.runways
    val maxDelayTime = positiveInteger.to(agenda.maximumDelayTime)

    println(allocateRunways(aircrafts, runways, maxDelayTime))
    
  def allocateRunways(aircrafts: List[Aircraft], runways: List[Runway], maxDelayTime: Int): List[Operation] =
    val aircraftQueue: Queue[Aircraft] = Queue(aircrafts*)
    val runwayQueue: Queue[Runway] = Queue(runways*)
    allocateHelper(aircraftQueue, runwayQueue, List.empty[Operation], 0, maxDelayTime).reverse

  def allocateHelper(aircrafts: Queue[Aircraft], 
                    runways: Queue[Runway], 
                    acc: List[Operation], 
                    currentTime: Long, 
                    maxDelayTime: Int): List[Operation] =
    if (aircrafts.isEmpty || runways.isEmpty)
      acc
    else
      val newAircraft = aircrafts.front
      //val feasibleWindowTime = newAircraft.targetTime + maxDelayTime
      val feasibleWindowTime = if (newAircraft.emergency.isDefined) newAircraft.targetTime + newAircraft.emergency.fold(ifEmpty = 0)(f = positiveInteger.to(_)) else newAircraft.targetTime + maxDelayTime
      
      if (feasibleWindowTime >= currentTime)
        val availableRunways = runways.filter { runway =>
          val runwayOperations = acc.filter(_.runway == runway)
          val isAvailable = runwayOperations.forall { operation =>
            val prevAircraft = operation.aircraft
            val timeDiff = calculateSeparation(prevAircraft, newAircraft)
            val sepMin = getSepOp(prevAircraft.classNumber, newAircraft.classNumber)
            timeDiff >= sepMin
          }
          isAvailable
        }

        availableRunways.headOption match
          case Some(runway) =>
            val allocation = Operation(newAircraft, runway, newAircraft.targetTime+currentTime)
            val auxRun = runways.dequeue
            runways.enqueue(auxRun)
            allocateHelper(aircrafts.drop(1), runways, allocation :: acc, newAircraft.targetTime+currentTime, maxDelayTime)
          case None =>
            allocateHelper(aircrafts, runways.drop(1), acc, newAircraft.targetTime+currentTime, maxDelayTime)
      else
        allocateHelper(aircrafts.drop(1), runways, acc, newAircraft.targetTime+currentTime, maxDelayTime)
