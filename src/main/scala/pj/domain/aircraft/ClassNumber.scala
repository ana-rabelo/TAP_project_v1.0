package pj.domain.aircraft

sealed trait ClassNumber 

/* 1 - Landing and Small */
case object Class1 extends ClassNumber

/* 2 - Landing and Large */
case object Class2 extends ClassNumber

/* 3 - Landing and Heavy */
case object Class3 extends ClassNumber

/* 4 - Take-off and Small */
case object Class4 extends ClassNumber

/* 5 - Take-off and Large */
case object Class5 extends ClassNumber

/* 6 - Take-off and Heavy */
case object Class6 extends ClassNumber
