package pj.property
import org.scalacheck.Properties

object SchedulePropertyTest extends Properties("Schedule"):

    property("in a valid schedule every aircraft was assigned a runway") = ???

    property("each aircraft should be scheduled for a runway which can handle it") = ???

    property("an aircraft can never be scheduled before its target time") = ???

