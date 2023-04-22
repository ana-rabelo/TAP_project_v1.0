package pj.domain

type Result[A] = Either[DomainError,A]

enum DomainError:
  case IOFileProblem(error: String)
  case XMLError(error: String)
  case InvalidPositiveInteger(e: Integer)
  case InvalidNonNegativeInteger(e: Integer)
