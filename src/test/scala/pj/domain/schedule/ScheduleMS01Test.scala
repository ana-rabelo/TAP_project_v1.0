package pj.domain.schedule

import org.scalatest.funsuite.AnyFunSuite
import pj.domain.agenda.Agenda
import pj.io.AgendaIO
import org.scalacheck.Prop.forAll
import pj.io.FileIO
import scala.xml.Elem
import pj.domain.schedule.ScheduleAircrafts.scheduleAgenda
import pj.domain.SimpleTypes.positiveInteger
import org.scalacheck.Prop

class ScheduleMS01Test extends AnyFunSuite:

  test("scheduleAgenda should produce the correct XML output") {
    val agenda = AgendaIO.readAgenda(s"files/assessment/ms01/valid02_in.xml").getOrElse(Agenda(List.empty, List.empty, positiveInteger.from(0).getOrElse(positiveInteger.zero)))
    val expectedXml: Elem = FileIO.load("files/assessment/ms01/valid02_out.xml").getOrElse(<error/>)
    assert(Right(scheduleAgenda(agenda)) == Right(expectedXml))
  }
