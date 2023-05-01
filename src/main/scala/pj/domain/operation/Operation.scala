package pj.domain.operation

import pj.domain.aircraft.Aircraft
import pj.domain.runway.Runway
import pj.domain.Result
import pj.domain.DomainError.*
import pj.io.FileIO.save
import pj.assessment.AssessmentMS01.create
import pj.domain.schedule.ScheduleAircrafts

import scala.xml.MetaData
import scala.xml.UnprefixedAttribute
import scala.xml.TopScope
import scala.xml.Elem
import scala.xml.Text

import scala.math.Numeric.ByteIsIntegral
import scala.xml.PrefixedAttribute
import scala.xml.NamespaceBinding
import scala.xml.Attribute

final case class Operation(aircraft: Aircraft, runway: Runway, time: Long, cost: Long) 

object Operation:

  val empty: Operation = Operation(Aircraft.empty, Runway.empty, 0L, 0L)

  def operationsToXml(schedule: ScheduleAircrafts): Result[Elem] = {
    val ops = schedule.operations

    val aircraftElems = ops.map { op =>
      val attrs = Map(
        "id" -> op.aircraft.id.toString,
        "runway" -> op.runway.id.toString,
        "time" -> op.time.toString
      )
      val meta = attrs.foldRight[MetaData](xml.Null) { case ((k, v), operations) =>
        new UnprefixedAttribute(k, Text(v), operations)
      }
      Elem(null, "aircraft", meta, TopScope, true)
    }

    val xmlnsAttr = new UnprefixedAttribute("xmlns", "http://www.dei.isep.ipp.pt/tap-2023", xml.Null)
    val xmlnsXsiAttr = new UnprefixedAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", xmlnsAttr)
    val schemaLocationAttr = new PrefixedAttribute("xsi", "schemaLocation", "http://www.dei.isep.ipp.pt/tap-2023 ../../schedule.xsd", xmlnsXsiAttr)
    
    val costAttr = new UnprefixedAttribute("cost", ops.map(_.cost).sum.toString, schemaLocationAttr)
    
    val scheduleElem = Elem(null, "schedule", costAttr, TopScope, true, aircraftElems*)
    
    scheduleElem match
      case Elem(_, label, _, _, children @ _*) => create(scheduleElem)
      case _ => Left(XMLError(s"Error creating XML schedule."))
  }