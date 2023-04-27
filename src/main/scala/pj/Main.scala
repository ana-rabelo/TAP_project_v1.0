import pj.io.readAgenda
import pj.domain.schedule.ScheduleMS01

object Main:
  def main(args: Array[String]): Unit =
    val agendaResult = readAgenda("files/assessment/ms01/valid00_in.xml")
    
    agendaResult match
        case Left(error) => println(s"$error")
        case Right(agenda) => ScheduleMS01.scheduleAgenda(agenda)