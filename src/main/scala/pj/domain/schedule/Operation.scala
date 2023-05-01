package pj.domain.schedule

import pj.domain.aircraft.Aircraft
import pj.domain.runway.Runway
import pj.constraints.SepOp
import scala.compiletime.ops.int

final case class Operation(aircraft: Aircraft, runway: Runway, time: Long) 