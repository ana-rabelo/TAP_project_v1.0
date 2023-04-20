package pj.domain.entity

/**
  * @param aircraft Aircraft to be scheduled
  * @param time Maximum delay allowed (900 the max)
  * @param runway Runway to be schedule
  */

final case class Schedule(  aircraft: Aircraft, time: Integer, runway: Runway)
