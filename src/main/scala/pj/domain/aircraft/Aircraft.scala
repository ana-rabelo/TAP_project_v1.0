package pj.domain.aircraft

import pj.domain.SimpleTypes.*
import pj.domain.aircraft.ClassNumber

/**
 * Represents the plane
 * 
 * It has an identifier, a class number, and the target time for the operation
 * An aircraft can issue an emergency, which reduces its maximum delay time
 */
final case class Aircraft(id: String, classNumber: ClassNumber, targetTime: Long, emergency: Option[positiveInteger])

object Aircraft{
    val empty: Aircraft = Aircraft("", ClassNumber.empty, 0L, positiveInteger.from(0).toOption)
}