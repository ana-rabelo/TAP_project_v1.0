package pj.io

import pj.domain.runway.Runway
import pj.domain.aircraft.Aircraft
import pj.domain.agenda.Agenda
import pj.domain.SimpleTypes.positiveInteger
import pj.domain.aircraft.{Class1, Class2, Class3, Class4, Class5, Class6, ClassNumber}
import pj.domain.DomainError.*
import pj.domain.Result
import pj.xml.XML.*

import scala.util.Try
import scala.xml.*

/**
 *  extracts information from the XML file to create an agenda object
 */
object AgendaIO:

  /**
   *  determine which ClassNumber corresponds to the string in the xml file
   *  
   * @param classStr a string representing the aircraft class
   * @return the corresponding ClassNumber or a domain error
   */
  def parseClassNumber(classStr: String): Result[ClassNumber] = classStr match
    case "1" => Right(Class1)
    case "2" => Right(Class2)
    case "3" => Right(Class3)
    case "4" => Right(Class4)
    case "5" => Right(Class5)
    case "6" => Right(Class6)
    case invalidClass => Left(InvalidClassNumber(s"Invalid aircraft class: $invalidClass"))

    /**
     *  extracts information from the XML file to create an aircraft object
     *  
     * @param xml the xml node to parse
     * @return the parsed aircraft object or a domain error
     */
  def parseAircraft(xml: Node): Result[Aircraft] =
    for {

      id <- fromAttribute(xml, "id")
      classNumber <- fromAttribute(xml, "class").flatMap(parseClassNumber)
      target <- fromAttribute(xml, "target").map(_.toInt)
      emergency <- Right(fromAttribute(xml, "emergency").flatMap(s => positiveInteger.from(s.toInt)).toOption)

    } yield Aircraft(id, classNumber, target, emergency)

  /**
    * extracts information from the XML file to create a runway object
    * 
    * @param xml the xml node to parse
    * @return the parsed runway object or a domain error
    */
  def parseRunway(xml: Node): Result[Runway] =
    for {
      
      id <- fromAttribute(xml, "id")
      handles <- traverse((xml \ "handles").map(_. \@("class")), parseClassNumber).map(_.toSet)

    } yield Runway(id, handles)

  /**
    * extracts information from the XML file to create an agenda object
    *
    * @param xml the xlm node to parse
    * @return the parsed agenda object or a domain error
    */
  def parseAgenda(xml: Node): Result[Agenda] =
    for {

      maximumDelayTime <- fromAttribute(xml, "maximumDelayTime").flatMap(s => positiveInteger.from(s.toInt))
      aircrafts <- traverse((xml \ "aircrafts" \ "aircraft").toList, parseAircraft)
      runways <- traverse((xml \ "runways" \ "runway").toList, parseRunway)

    } yield Agenda(aircrafts, runways, maximumDelayTime)

  /**
   * reads the agenda from the xml file
   * 
   * @param filePath the path to the xml file
   * @return the parsed agenda object or a domain error
   */
  def readAgenda(filePath: String): Result[Agenda] =
    for {
      xml <- FileIO.load(filePath)
      agenda <- parseAgenda(xml)
    } yield agenda