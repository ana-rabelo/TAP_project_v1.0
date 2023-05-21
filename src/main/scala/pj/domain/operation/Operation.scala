package pj.domain.operation

import pj.domain.aircraft.Aircraft
import pj.domain.runway.Runway

final case class Operation(
    aircraft: Aircraft,
    runway: Runway,
    time: Long,
    cost: Long
)

object Operation:
  val empty: Operation = Operation(Aircraft.empty, Runway.empty, 0L, 0L)