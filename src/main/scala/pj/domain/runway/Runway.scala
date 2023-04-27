package pj.domain.runway

import pj.domain.aircraft.ClassNumber
import pj.domain.aircraft.Aircraft

/**
* Represents the physical runway
*
* It has an identifier, a set of class numbers, which it can
* handle. Some runways, due to their length are limited to some classes of aircraft
*/
final case class Runway(id: String, handles: Set[ClassNumber])