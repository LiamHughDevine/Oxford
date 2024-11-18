/** The trait for a Sleeping Tutor protocol. */
trait SleepingTutor{
  /** A tutor waits for students to arrive. */
  def tutorWait: Unit

  /** A student arrives and waits for the tutorial. */
  def arrive: Unit

  /** A student receives a tutorial. */
  def receiveTute: Unit

  /** A tutor ends the tutorial. */
  def endTeach: Unit
}

// --------------

import scala.util.Random
import ox.scl._

object SleepingTutorSimulation{
  // Some implementation of SleepingTutor
  private val st: SleepingTutor = new SleepingTutorMonitor
  //private val st: SleepingTutor = new SleepingTutorSemaphore

  private var iters = 2

  private def student(me: String) = thread("Student"+me){
    var i = 0
    while(i < iters){
      Thread.sleep(Random.nextInt(2000))
      println("Student "+me+" arrives")
      st.arrive
      println("Student "+me+" ready for tutorial")
      st.receiveTute
      println("Student "+me+" leaves")
      Thread.sleep(2000)
      i += 1
    }
  }

  private def tutor = thread("Tutor"){
    var i = 0
    while(i < iters){
      println("Tutor waiting for students")
      st.tutorWait
      println("Tutor starts to teach")
      Thread.sleep(1000)
      println("Tutor ends tutorial")
      st.endTeach
      Thread.sleep(1000)
      i += 1
      println()
    }
  }

  def system = tutor || student("Alice") || student("Bob")

  def main(args: Array[String]) = run(system)
}