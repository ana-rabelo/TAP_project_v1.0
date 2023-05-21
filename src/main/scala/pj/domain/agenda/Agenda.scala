package pj.domain.agenda

import pj.domain.SimpleTypes.*
import pj.domain.aircraft.*
import pj.domain.runway.*

/**
 * Represents the Aircrafts to operate and available Runways.
 * 
 * It has a list of aircrafts, a list of runways and a maximum delay time.
 */
final case class Agenda(aircrafts: List[Aircraft], runways: List[Runway], maximumDelayTime: positiveInteger)
