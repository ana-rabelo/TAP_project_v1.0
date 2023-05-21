package pj.domain

import pj.domain.aircraft.ClassNumber

type Result[A] = Either[DomainError,A]

enum DomainError:

  case IOFileProblem(error: String)
  case XMLError(error: String)
  case InvalidPositiveInteger(e: Integer)
  case InvalidNonNegativeInteger(e: Integer)
  case InvalidClassNumber(e: String)
  case InvalidId(e: String)
  case OperationError(e: String)
  case ScheduleError(e: String)
  case NoRunwaysAvailable(classNumber: String)
  case RepeatedAircraftId(id: String)
  case RepeatedRunwayId(id: String)
  case MaximumTimeWindowExceeded(id: String, time: Long)