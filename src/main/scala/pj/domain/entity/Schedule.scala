package pj.domain.entity

/**
* Contains the Aircraft and their specific Runway and operation time
* 
* This is the result of the scheduling process
*/
final case class Schedule(aircraft: Aircraft, time: Int, runway: Runway)
