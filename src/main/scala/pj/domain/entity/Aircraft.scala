package pj.domain.entity

final case class Aircraft(identifier: String, classType: Int, targetTime: Long, emergency: Option[Integer])