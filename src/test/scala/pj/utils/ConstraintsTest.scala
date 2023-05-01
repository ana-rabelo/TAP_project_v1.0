package pj.utils

import pj.domain.runway.Runway
import pj.domain.aircraft.Aircraft
import pj.domain.operation.Operation
import pj.domain.aircraft.Class1
import pj.domain.aircraft.Class2
import pj.domain.aircraft.Class3
import pj.domain.aircraft.Class4
import pj.domain.aircraft.Class5
import pj.domain.aircraft.Class6
import pj.domain.SimpleTypes.positiveInteger

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.immutable.Queue
import org.scalacheck.{Gen, Prop as Properties}

class ConstraintsTest extends AnyFunSuite:

    import pj.utils.Constraints.*
    
    test("isAircraftCompatibleWithRunway should return true if the runway handles the aircraft") {
        val aircraft = Aircraft("A1",Class1, 0L,positiveInteger.from(0).toOption)
        val runway = Runway("R1",Set(Class1, Class2))
        assert(isAircraftCompatibleWithRunway(aircraft, runway))
    }

    test("isAircraftCompatibleWithRunway should return false if the runway does not handle the aircraft") {
        val aircraft = Aircraft("A1", Class3, 0L, positiveInteger.from(0).toOption)
        val runway = Runway("R1", Set(Class1, Class2))
        assert(!isAircraftCompatibleWithRunway(aircraft, runway))
    }

    test("calculateSeparation should return the difference in target times between the two aircrafts") {
        val prevAircraft = Aircraft("A1",Class1, 100L, positiveInteger.from(0).toOption)
        val aircraft = Aircraft("A2",Class2, 150L, positiveInteger.from(0).toOption)
        assert(calculateSeparation(prevAircraft, aircraft) == 50L)
    }

    test("getSmallSep should return the smallest separation between the aircraft and the last runway operations") {
        val aircraft = Aircraft("A1",Class2, 200L, positiveInteger.from(0).toOption)
        val op1 = Operation(Aircraft("A2",Class1, 150L, positiveInteger.from(0).toOption), Runway("R1", Set(Class1)), 0L, 0L)
        val op2 = Operation(Aircraft("A3",Class2, 139L, positiveInteger.from(0).toOption), Runway("R2", Set(Class2)), 0L, 0L)
        val acc = List(op1, op2)
        val runways = Queue(Runway("R1", Set(Class1)), Runway("R2", Set(Class2)))
        assert(getSmallSep(aircraft, acc, runways) == 8L)
    }

    test("reverseOperation should return the last operation and a new list without the last operation") {
        val op1 = Operation(Aircraft("A2",Class1, 100L, positiveInteger.from(0).toOption), Runway("R1", Set(Class1)), 0L, 0L)
        val op2 = Operation(Aircraft("A3",Class1, 150L, positiveInteger.from(0).toOption), Runway("R2", Set(Class2)), 0L, 0L)
        val acc = List(op1, op2)
        val (lastOp, newAcc) = reverseOperation(acc)
        assert(lastOp == op2)
        assert(newAcc == List(op1))
    }

    test("insertFirst should insert an element at the first position of the queue") {
        val queue = Queue(Aircraft("A1",Class1, 100L, positiveInteger.from(0).toOption), Aircraft("A2",Class2, 250L, positiveInteger.from(0).toOption))
        val newQueue = insertFirst(queue.reverse, Aircraft("A3",Class1, 40L, positiveInteger.from(0).toOption))
        assert(newQueue == Queue(Aircraft("A3",Class1, 40L, positiveInteger.from(0).toOption), Aircraft("A1",Class1, 100L, positiveInteger.from(0).toOption), Aircraft("A2",Class2, 250L, positiveInteger.from(0).toOption)))
    }

    test("calculatePenalty should return the penalty for the delay of an operation") {
        assert(calculatePenalty(Class1, 0L) == 0L)
        assert(calculatePenalty(Class3, 20L) == 40L)
        assert(calculatePenalty(Class6, 30L) == 30L)
    }

    test("getSepOp should return the minimum separation value between two aircrafts") {
        val genClassNumber = Gen.oneOf(Class1, Class2, Class3, Class4, Class5, Class6)
        val prop = for {
        trailing <- genClassNumber
        leading <- genClassNumber
        sepValue = getSepOp(trailing, leading)((trailing, leading))
        } yield assert(sepValue >= 60L && sepValue <= 196L)
    }

    test("getSepOp should return the expected separation value for specific pairs of aircrafts") {
        val testCases = Seq(
        ((Class1, Class1), 82L),
        ((Class1, Class2), 131L),
        ((Class1, Class3), 196L),
        ((Class1, Class4), 60L),
        ((Class1, Class5), 60L),
        ((Class1, Class6), 60L),

        ((Class2, Class1), 69L),
        ((Class2, Class2), 69L),
        ((Class2, Class3), 157L),
        ((Class2, Class4), 60L),
        ((Class2, Class5), 60L),
        ((Class2, Class6), 60L),

        ((Class3, Class1), 60L),
        ((Class3, Class2), 60L),
        ((Class3, Class3), 96L),
        ((Class3, Class4), 60L),
        ((Class3, Class5), 60L),
        ((Class3, Class6), 60L),

        ((Class4, Class1), 75L),
        ((Class4, Class2), 75L),
        ((Class4, Class3), 75L),
        ((Class4, Class4), 60L),
        ((Class4, Class6), 120L),
        ((Class4, Class5), 60L),

        ((Class5, Class1), 75L),
        ((Class5, Class2), 75L),
        ((Class5, Class3), 75L),
        ((Class5, Class4), 60L),
        ((Class5, Class5), 60L),
        ((Class5, Class6), 120L),

        ((Class6, Class1), 75L),
        ((Class6, Class2), 75L),
        ((Class6, Class3), 75L),
        ((Class6, Class4), 60L),
        ((Class6, Class5), 60L),
        ((Class6, Class6), 90L),
        )

        for ((pair, expectedSep) <- testCases)
            val sepValue = getSepOp(pair._1, pair._2)(pair)
            assert(sepValue == expectedSep)
    }
