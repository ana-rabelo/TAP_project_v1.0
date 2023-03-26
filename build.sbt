name := "tap-2023-base"

version := "0.1"

scalaVersion := "3.2.2"

scalacOptions ++= Seq("-source:future", "-indent", "-rewrite")

// XML
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"
// ScalaTest
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.15"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test"
// ScalaCheck
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.17.0" % "test"

wartremoverErrors ++= Warts.allBut(Wart.Any, Wart.Equals, Wart.Nothing,
  Wart.Overloading, Wart.Recursion, Wart.StringPlusAny,
  Wart.ToString, Wart.TripleQuestionMark)
