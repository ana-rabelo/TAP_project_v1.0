package pj.domain

import DomainError.*
import scala.annotation.targetName

// Define simple types for positiveInteger and nonNegativeInteger
object SimpleTypes :

    type Result[A] = Either[DomainError, A]

    opaque type positiveInteger = Int
    object positiveInteger:
        def from(value: Int): Result[positiveInteger] =
            if(value > 0) Right(value) else Left(InvalidPositiveInteger(value))
        
        def to(value: positiveInteger): Int = value

        def toLong(value: positiveInteger): Long = value.toLong

    opaque type nonNegativeInteger = Int
    object nonNegativeInteger:
        def from(value: Int): Result[nonNegativeInteger] =
            if(value >= 0) Right(value) else Left(InvalidNonNegativeInteger(value))
        
        def to(value: nonNegativeInteger): Int = value