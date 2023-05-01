package pj.domain.runway

import pj.domain.aircraft.ClassNumber
import pj.domain.aircraft.Aircraft
import pj.utils.Constraints.* 
import pj.domain.operation.Operation

/**
* Represents the physical runway
*
* It has an identifier, a set of class numbers, which it can
* handle. Some runways, due to their length are limited to some classes of aircraft
*/
final case class Runway(id: String, handles: Set[ClassNumber])

object Runway:
  val empty: Runway = Runway("", Set.empty)
