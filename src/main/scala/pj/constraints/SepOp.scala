package  pj.constraints

import pj.domain.aircraft.{Class3, ClassNumber, Class4, Class2, Class6, Class1, Class5}

object SepOp {

    /**
    * Returns the separation time for an operation between two given Aircraft number classes on the same runway.
    * @param trailing The trailing Aircraft number class.
    * @param leading The leading Aircraft number class.
    * @return An integer representing the separation in seconds for the operation.
    */
    def getSepOp(trailing: ClassNumber, leading: ClassNumber): Int =
        (trailing, leading) match
            case (Class1, Class1) => 82
            case (Class1, Class2) => 131
            case (Class1, Class3) => 196
            case (Class1, _) => 60

            case (Class2, Class1) => 69
            case (Class2, Class2) => 69
            case (Class2, Class3) => 157
            case (Class2, _) => 60
            
            case (Class3, Class1) => 60
            case (Class3, Class2) => 60
            case (Class3, Class3) => 96
            case (Class3, _) => 60
            
            case (Class4, Class4) => 60
            case (Class4, Class5) => 60
            case (Class4, Class6) => 120
            case (Class4, _) => 75
            
            case (Class5, Class4) => 60
            case (Class5, Class5) => 60
            case (Class5, Class6) => 120
            case (Class5, _) => 75
            
            case (Class6, Class4) => 60
            case (Class6, Class5) => 60
            case (Class6, Class6) => 90
            case (Class6, _) => 75
}