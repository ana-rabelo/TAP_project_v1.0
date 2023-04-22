import pj.io.readAgenda

object Main:
  def main(args: Array[String]): Unit =
    val agendaResult = readAgenda("files/assessment/ms01/valid00_in.xml")
    
    agendaResult match
        case Left(error) => println(s"$error")
        case Right(agenda) => println(s"Agenda: $agenda")