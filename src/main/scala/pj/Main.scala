import pj.io.AgendaIO.*
import pj.domain.schedule.ScheduleAircrafts.*

object Main:
  def main(args: Array[String]): Unit =
    val agendaResult = readAgenda("files/assessment/ms01/valid02_in.xml")
    
    agendaResult match
        case Left(error) => println(s"$error")
        case Right(agenda) => scheduleAgenda(agenda)