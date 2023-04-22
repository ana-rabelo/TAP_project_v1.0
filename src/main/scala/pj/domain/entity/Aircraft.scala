package pj.domain.entity

import pj.domain.SimpleTypes.*
/**
 * Represents the plane
 * 
 * It has an identifier, a class number, and the target time for the operation
 * An aircraft can issue an emergency, which reduces its maximum delay time
 */
final case class Aircraft(id: String, classNumber: ClassNumber, targetTime: Long, emergency: Option[positiveInteger])