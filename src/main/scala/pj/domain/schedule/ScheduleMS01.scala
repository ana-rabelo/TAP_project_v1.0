package pj.domain.schedule

import scala.xml.Elem
import scala.xml.*
import pj.domain.Result
import pj.io.FileIO.save

object ScheduleMS01 extends Schedule:

  /**
   * Save a xml file with the schedule of the aircrafts.
   * 
   * @param xml the xml elem with the ScheduleAircrafts
   * @return a domain error or the xml wrapped in a Right object
   */
  def create(xml: Elem): Result[Elem] =
    save("files/test/ms01/valid02_out.xml", xml)
    Right(xml)