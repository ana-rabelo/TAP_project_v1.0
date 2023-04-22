package pj.domain.entity

import pj.domain.SimpleTypes.*
/**
* Contains the Aircraft and their specific Runway and operation time
* 
* This is the result of the scheduling process
*/
final case class Schedule(aircraft: Aircraft, time: nonNegativeInteger, runway: Runway)
