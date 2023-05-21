package pj.property
import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import org.scalacheck.Test.Result
import pj.property.Generators.agendaGen
import pj.domain.schedule.ScheduleAircrafts.scheduleAgenda
import pj.domain.DomainError.ScheduleError
import pj.domain.schedule.ScheduleAircrafts.*
import pj.domain.aircraft.ClassNumber
import pj.domain.aircraft.{Class1, Class2, Class3,Class4, Class5, Class6}
import pj.utils.Constraints.{calculateSeparation, getSepOp}

object SchedulePropertyTest extends Properties("Schedule"):

    property("in a valid schedule every aircraft was assigned a runway") = 
        forAll(agendaGen){ 
            agenda =>
                val schedule = scheduleAgenda(agenda)
                schedule.fold(_ => false, validSchedule =>
                    agenda.aircrafts.sizeIs == validSchedule.operations.size)
        }

    property("an aircraft can never be scheduled before its target time") = 
        forAll(agendaGen) { 
            agenda =>
                val schedule = scheduleAgenda(agenda)
                schedule.fold(_ => false, validSchedule =>
                    validSchedule.operations.forall { operation =>
                        operation.aircraft.targetTime <= operation.time}
                )
        }

    property("for an aircraft there is allocated late, penalties must be applied correctly according to the type of operation") = 
        forAll(agendaGen) { 
            agenda =>
                val schedule = scheduleAgenda(agenda)
                schedule.fold(_ => false, validSchedule => 
                    validSchedule.operations.forall { operation =>
                        (operation.time - operation.aircraft.targetTime) match
                            case 0 => true
                            case _ =>
                                if (Set(Class1, Class2, Class3).contains(operation.aircraft.classNumber))
                                    operation.cost*2 == (operation.time - operation.aircraft.targetTime)*2
                                else 
                                    operation.cost == (operation.time - operation.aircraft.targetTime)
                        })
        }

    property("each aircraft should be scheduled for a runway which can handle it") =
        forAll(agendaGen) { 
            agenda =>
                val schedule = scheduleAgenda(agenda)
                schedule.fold(_ => false, validSchedule =>
                    validSchedule.operations.forall { operation =>
                        validSchedule.operations.exists(op =>
                            op.runway.handles.contains(operation.aircraft.classNumber))}
                )
        }

    property("in a valid schedule the non emergency aircrafts should be allocated in a first come first served way") = 
        forAll(agendaGen) { 
            agenda =>
                val schedule = scheduleAgenda(agenda)
                schedule.fold(_ => false, validSchedule =>
                    validSchedule.operations.filter(_.aircraft.emergency.isEmpty) match {
                        case Nil => true
                        case nonEmergencyAircrafts =>
                            val sortedByTargetTime = nonEmergencyAircrafts.sortBy(_.time)
                            sortedByTargetTime == nonEmergencyAircrafts
                    })
        }

    property("the aircrafts scheduled in the same runway should have a minimum separation time") = 
        forAll(agendaGen) { 
            agenda =>
                scheduleAgenda(agenda).fold(_ => false, validSchedule =>
                    validSchedule.operations.forall { operation =>
                        val aircraft = operation.aircraft
                        val runway = operation.runway
                        val otherOperations = validSchedule.operations.filter(op =>
                            op.runway == runway && op.aircraft.id != aircraft.id
                        )
                        
                        val timeDifferences = otherOperations.map(otherOp => calculateSeparation(aircraft, otherOp.aircraft))
                        val maximumTimeDifference = otherOperations.map(otherOp =>
                            getSepOp(otherOp.aircraft.classNumber, aircraft.classNumber)((otherOp.aircraft.classNumber), (aircraft.classNumber))
                        )

                        timeDifferences.zip(maximumTimeDifference).forall {
                            case (timeDiff, maxTimeDiff) =>
                            timeDiff < 0 || timeDiff >= maxTimeDiff
                        }
                    }
                )
        }