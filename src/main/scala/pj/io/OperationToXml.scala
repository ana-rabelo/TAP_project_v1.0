package pj.io

import pj.assessment.AssessmentMS01.create
import pj.domain.aircraft.Aircraft
import pj.domain.DomainError
import pj.domain.Result
import pj.domain.runway.Runway
import pj.domain.schedule.ScheduleAircrafts
import pj.io.FileIO.save

import scala.xml.Elem
import scala.xml.MetaData
import scala.xml.PrefixedAttribute
import scala.xml.PrettyPrinter
import scala.xml.TopScope
import scala.xml.Text
import scala.xml.UnprefixedAttribute
import scala.xml.Utility
import scala.xml.XML

object OperationToXml:

    def operationsToXml(schedule: ScheduleAircrafts): Result[Elem] =
        val ops = schedule.operations.sortBy(_.time)

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
        val schemaLocationAttr = new PrefixedAttribute("xsi", "schemaLocation", "http://www.dei.isep.ipp.pt/tap-2023 ../../schedule.xsd ", xmlnsXsiAttr)
        val costAttr = new UnprefixedAttribute("cost", ops.map(_.cost).sum.toString, schemaLocationAttr)
            
        val scheduleElem = Elem(null, "schedule", costAttr, TopScope, true, aircraftElems*)

        Right(generateXmlFormat(scheduleElem))

    def errorToXml(error: DomainError): Result[Elem] =
        val errorXml = new UnprefixedAttribute("message", error.toString(), xml.Null)
        val schemaLocationAttrError = new PrefixedAttribute("xsi", "schemaLocation", "http://www.dei.isep.ipp.pt/tap-2023 ../../scheduleError.xsd ", errorXml)
        val xmlnsXsiAttr = new UnprefixedAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", schemaLocationAttrError)
        val xmlnsAttr = new UnprefixedAttribute("xmlns", "http://www.dei.isep.ipp.pt/tap-2023", xmlnsXsiAttr)

        val scheduleElem = Elem(null, "ScheduleError", xmlnsAttr, TopScope, minimizeEmpty = true)
        Right(generateXmlFormat(scheduleElem))

    def generateXmlFormat(scheduleElem: Elem): Elem =
            
        val printer = new PrettyPrinter(80, 3).format(scheduleElem)
        val formattedScheduleElem = Utility.trim(scheduleElem)
        val xmlString = printer.format(formattedScheduleElem)

        XML.loadString(xmlString)