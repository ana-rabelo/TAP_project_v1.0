package pj.utils

import pj.domain.aircraft.Aircraft
import pj.domain.runway.Runway
import scala.collection.immutable.Queue
import pj.domain.operation.Operation
import pj.domain.aircraft.*

object Constraints: 

    /**
     * Check if the runway can handle the aircraft.
     * 
     * @param aircraft the aircraft to check
     * @param runway the runway to check
     * 
     * @return true if the runway can handle the aircraft, false otherwise
     */
    def isAircraftCompatibleWithRunway(aircraft: Aircraft, runway: Runway): Boolean =
        runway.handles.contains(aircraft.classNumber)

    /**
     * Calculate the separation between two aircrafts.
     * 
     * @param prevAircraft the previous aircraft
     * @param aircraft the aircraft
     * 
     * @return the separation value between the two aircrafts
     */
    def calculateSeparation(prevAircraft: Aircraft, aircraft: Aircraft): Long =
        aircraft.targetTime - prevAircraft.targetTime

    /**
     * Get the smallest separation between the aircraft and the last runways operations.
     * 
     * @param aircraft the aircraft to check
     * @param acc the list of operations
     * @param runways the queue of runways
     * 
     * @return the smallest separation
     */
    def getSmallSep(aircraft: Aircraft, acc: List[Operation], runways: Queue[Runway]): Long =
        val runwayOperations = runways.map { runway =>
            acc.filter(_.runway == runway).reverse.lastOption
        }.flatten

        val sepMin = runwayOperations.map { operation =>
            val sepValue = getSepOp(aircraft.classNumber, operation.aircraft.classNumber)((aircraft.classNumber),(operation.aircraft.classNumber))
            sepValue - calculateSeparation(operation.aircraft, aircraft)
        }
        
        sepMin.foldLeft(Long.MaxValue)(_ min _)

    /**
     * Creates a new list of operations without the new operation.
     * 
     * @param acc the list of operations
     * 
     * @return a tuple with the last operation and the new list of operations
     */
    def reverseOperation(acc: List[Operation]): (Operation, List[Operation]) =
        val lastOperation = acc.lastOption.getOrElse(Operation.empty)
        (lastOperation, acc.dropRight(1))

    /**
     * Insert an element at the first position of the queue creating a new queue.
     * 
     * @param queue the queue
     * @param element the element to insert
     * 
     * @return the new queue with the element in head
     */
    def insertFirst[T](queue: Queue[T], element: T): Queue[T] =
        if (queue.isEmpty)
            Queue(element)
        else
            val (head, tail) = queue.dequeue
            insertFirst(tail, element).enqueue(head)
   
    /**
     * Calculate the penalty for the delay of an operation.
     * 
     * @param operation the class number of the operation
     * @param delay the delay of the operation
     * 
     * @return the penalty
     */
    def calculatePenalty(operation: ClassNumber, delay: Long): Long =
    if (delay <= 0) 0
    else
      val penalty = operation match
        case Class1 | Class2 | Class3 => 2
        case _ => 1
      penalty * delay

    /**
     * Get the minimum separation value between two aircrafts.
     * 
     * @param trailing the trailing aircraft
     * @param leading the leading aircraft
     * 
     * @return a map with the minimum separation and the aircrafts class numbers
     */
    //TODO: implement the matrix with the values
    def getSepOp(trailing: ClassNumber, leading: ClassNumber): Map[(ClassNumber, ClassNumber), Long] = Map (
        (Class1, Class1) -> 82,
        (Class1, Class2) -> 131,
        (Class1, Class3) -> 196,
        (Class1, Class4) -> 60,
        (Class1, Class5) -> 60,
        (Class1, Class6) -> 60,

        (Class2, Class1) -> 69,
        (Class2, Class2) -> 69,
        (Class2, Class3) -> 157,
        (Class2, Class4) -> 60,
        (Class2, Class5) -> 60,
        (Class2, Class6) -> 60,

        (Class3, Class1) -> 60,
        (Class3, Class2) -> 60,
        (Class3, Class3) -> 96,
        (Class3, Class4) -> 60,
        (Class3, Class5) -> 60,
        (Class3, Class6) -> 60,

        (Class4, Class1) -> 75,
        (Class4, Class2) -> 75,
        (Class4, Class3) -> 75,
        (Class4, Class4) -> 60,
        (Class4, Class6) -> 120,
        (Class4, Class5) -> 60,

        (Class5, Class1) -> 75,
        (Class5, Class2) -> 75,
        (Class5, Class3) -> 75,
        (Class5, Class4) -> 60,
        (Class5, Class5) -> 60,
        (Class5, Class6) -> 120,

        (Class6, Class1) -> 75,
        (Class6, Class2) -> 75,
        (Class6, Class3) -> 75,
        (Class6, Class4) -> 60,
        (Class6, Class5) -> 60,
        (Class6, Class6) -> 90,
    )