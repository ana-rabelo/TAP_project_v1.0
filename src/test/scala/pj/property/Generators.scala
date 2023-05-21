package pj.property

import pj.domain.schedule.ScheduleAircrafts.scheduleAgenda
import pj.domain.agenda.Agenda
import pj.domain.runway.Runway
import pj.domain.SimpleTypes.positiveInteger
import pj.domain.aircraft.Aircraft
import pj.domain.aircraft.{Class1, Class2, Class3, Class4, Class5, Class6}
import pj.domain.aircraft.ClassNumber
import org.scalacheck.Gen

object Generators:
    val classes = List(Class1, Class2, Class3, Class4, Class5, Class6)

    /** Generates a aircraft ClassNumber */
    val classNumberGen: Gen[ClassNumber] =
        Gen.oneOf(classes)

    /** Generates a aircraft emergency value between 5 and 10 */
    val aircraftEmergencyGen: Gen[Option[positiveInteger]] =
        Gen.frequency(
            (10, Gen.const(None)),
            (0, Gen.choose(5, 10).map((x: Int) => positiveInteger.from(x).toOption))
        )

    /** Generates a aircraft id prefix */
    val idAircraftGen: Gen[String] =
        Gen.oneOf("Aircraft", "A", "")

    /** Generates a aircraft with a id, classNumber, targetTime and emergency option
     * value The emergency value is generated with a frequency of 7/10 for None and
     * 3/10 for Some
     */
    def aircraftGen(id: String): Gen[Aircraft] = for {
        classNumber <- classNumberGen
        targetTime <- Gen.choose(0, 200)
        emergency <- aircraftEmergencyGen
    } yield Aircraft(id, classNumber, targetTime, emergency)

    /** Generates a list of aircrafts with a size between 1 and 10 */
    val listAircraftGen: Gen[List[Aircraft]] = for {
        id <- idAircraftGen
        n <- Gen.chooseNum(2, 5)
        list <- Gen
            .listOfN(n, aircraftGen(id))
            .map(_.zipWithIndex)
        } yield list.map { case (aircraft, index) => aircraft.copy(id + ((index + 1).toString))
    }