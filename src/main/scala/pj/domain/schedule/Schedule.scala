package pj.domain.schedule

import pj.domain.Result
import scala.xml.Elem

trait Schedule:

  // list of aircrafts
  // cost

  def create(xml: Elem): Result[Elem]
