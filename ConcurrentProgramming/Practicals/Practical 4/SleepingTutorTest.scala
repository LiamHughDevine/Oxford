import scala.util.Random
import ox.scl._

object SleepingTutorTest{
  // Some implementation of SleepingTutor
  private val st: SleepingTutor = new SleepingTutorMonitor
  //private val st: SleepingTutor = new SleepingTutorSemaphore

  private var iters = 50
  private val log = new SharedLog[LogEvent](3)

  private def student(me: Int) = thread("Student"+me){
    var i = 0
    while(i < iters){
      Thread.sleep(Random.nextInt(20))
      log.add(me, StudentArrives(me))
      st.arrive
      st.receiveTute
      log.add(me, StudentLeaves(me))
      Thread.sleep(20)
      i += 1
    }
  }

  private def tutor(me: Int) = thread("Tutor"){
    var i = 0
    while(i < iters){
      st.tutorWait
      log.add(me, TuteStarts())
      Thread.sleep(10)
      log.add(me, TuteEnds())
      st.endTeach
      Thread.sleep(10)
      i += 1
    }
  }

  def system = tutor(2) || student(0) || student(1)

  def main(args: Array[String]) = {
    run(system)
    assert (checkLog(log.get))
    println("Success")
  }

  // Verifies that the log is correct
  def checkLog(events: Array[LogEvent]) : Boolean = {
    // Simulates where the tutor and students are
    var tutor = TutorStatus.waiting
    val students = Array.fill(2)(StudentStatus.home)
    var firstStudentLeft = false

    for (event <- events) {
      event match {
        case StudentArrives(student) =>
          if (students(student) != StudentStatus.home) return false
          students(student) = StudentStatus.tutorial
        case TuteStarts() =>
          // Verifies requirement 1 that both students have arrived before starting the tutorial
          if (students(0) != StudentStatus.tutorial) return false
          if (students(1) != StudentStatus.tutorial) return false
          if (tutor != TutorStatus.waiting) return false
          tutor = TutorStatus.teaching
        case TuteEnds() =>
          if (tutor != TutorStatus.teaching) return false
          tutor = TutorStatus.taught
        case StudentLeaves(student) =>
          if (students(student) != StudentStatus.tutorial) return false
          // Verifies the second requirement that the students leave only after the tutorial has ended
          if (tutor != TutorStatus.taught) return false
          students(student) = StudentStatus.home
          if (!firstStudentLeft) firstStudentLeft = true
          else { firstStudentLeft = false; tutor = TutorStatus.waiting } // Resets the Tutor
      }
    }
    true
  }
}

object StudentStatus extends Enumeration {
  val home, tutorial = Value
}

object TutorStatus extends Enumeration {
  val waiting, teaching, taught = Value
}