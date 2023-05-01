package pj.io

import org.scalatest.funsuite.AnyFunSuite

import pj.io.AgendaIO
import pj.domain.aircraft.{Class1, Class2, Class3, Class4, Class5, Class6, ClassNumber}
import pj.domain.aircraft.Aircraft
import pj.domain.SimpleTypes.positiveInteger
import pj.domain.runway.Runway
import pj.domain.agenda.Agenda
import pj.domain.DomainError.*
import pj.domain.DomainError

class AgendaIOTest extends AnyFunSuite:
    
    test("parseClassNumber should return ClassNumber when given a valid string") {
        assert(AgendaIO.parseClassNumber("1") == Right(Class1))
        assert(AgendaIO.parseClassNumber("2") == Right(Class2))
        assert(AgendaIO.parseClassNumber("3") == Right(Class3))
        assert(AgendaIO.parseClassNumber("4") == Right(Class4))
        assert(AgendaIO.parseClassNumber("5") == Right(Class5))
        assert(AgendaIO.parseClassNumber("6") == Right(Class6))
    }

    test("parseClassNumber should return a domain error when given an invalid string") {
        assert(AgendaIO.parseClassNumber("0").isLeft)
        assert(AgendaIO.parseClassNumber("7").isLeft)
        assert(AgendaIO.parseClassNumber("invalid").isLeft)
    }

    test("parseAircraft should return an Aircraft object when given valid xml") {
        val validXml = <aircraft id="AC1" class="1" target="100" emergency="50"/>

        val expected = Right(Aircraft("AC1", Class1, 100, positiveInteger.from(50).toOption))
        assert(AgendaIO.parseAircraft(validXml) == expected)
    }

    test("parseAircraft should return a domain error when given invalid xml") {
        val invalidXml = <aircraft id="AC1" class="7" target="100" emergency="50"/>

        assert(AgendaIO.parseAircraft(invalidXml).isLeft)
    }

    test("parseRunway should return a Runway object when given valid xml") {
        val validXml = <runway id="RW1"><handles class="1"/><handles class="2"/></runway>

        val expected = Right(Runway("RW1", Set(Class1, Class2)))
        assert(AgendaIO.parseRunway(validXml) == expected)
    }

    test("parseRunway should return a domain error when given invalid xml") {
        val invalidXml = <runway id="RW1"><handles class="7"/></runway>

        assert(AgendaIO.parseRunway(invalidXml).isLeft)
    }

    test("parseAgenda should return an Agenda object when given valid xml") {
        val filePath = "src/test/scala/pj/resources/valid-agenda.xml"

        val expected = Right(Agenda(
        List(Aircraft("AC1", Class1, 100, positiveInteger.from(50).toOption), Aircraft("AC2", Class2, 200, None)),
        List(Runway("RW1", Set(Class1, Class2)), Runway("RW2", Set(Class3, Class4))),
        positiveInteger.from(5).getOrElse(positiveInteger.zero)))

        assert(AgendaIO.readAgenda(filePath) == expected)
    }

    test("readAgenda returns an error from invalid XML file") {
        val filePath = "src/test/scala/pj/resources/invalid-agenda.xml"
        val expected = Left(InvalidClassNumber("Invalid aircraft class: 8"))
        assert(AgendaIO.readAgenda(filePath) == expected)
    }

    test("readAgenda returns an error from non-existent file") {
        val filePath = "non-existent-file.xml"
        val expected = Left(IOFileProblem("File not found: non-existent-file.xml"))
        assert(AgendaIO.readAgenda(filePath) == expected)
    }