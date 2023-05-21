package pj.domain.schedule

import scala.xml.Elem
import scala.xml.*

import pj.domain.Result
import pj.domain.DomainError.*
import pj.domain.schedule.ScheduleAircrafts.scheduleAgenda
import pj.domain.operation.*

import pj.io.FileIO.save
import pj.io.AgendaToXml.*
import pj.io.OperationToXml.*

object ScheduleMS01 extends Schedule:

  /**
   * Save a xml file with the schedule of the aircrafts.
   * 
   * @param xml the xml elem with the ScheduleAircrafts
   * @return a domain error or the xml wrapped in a Right object
   */
  def create(xml: Elem): Result[Elem] =
    val agendaResult = readAgenda(xml)

    agendaResult match
        case Left(error) => 
          agendaErrorToXml(error)

        case Right(agenda) => 
          val operations = scheduleAgenda(agenda)
          
          operations match
            case Left(error) => errorToXml(error)
            case Right(schedule) => operationsToXml(schedule)