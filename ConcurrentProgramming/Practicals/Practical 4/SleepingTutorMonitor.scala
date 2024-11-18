import ox.scl._

class SleepingTutorMonitor extends SleepingTutor {
  private val lock = new Lock
  private val tutorWaiting = lock.newCondition
  private val studentWaiting = lock.newCondition
  private val tuteHappening = lock.newCondition

  // Checks whether the other student has already either arrived or left
  private var firstArrived = false

  def tutorWait = lock.mutex { tutorWaiting.await() }

  def arrive = lock.mutex {
    if (firstArrived) { studentWaiting.signal(); tutorWaiting.signal(); firstArrived = false }
    else { firstArrived  = true; studentWaiting.await() }
  }

  def receiveTute = lock.mutex { tuteHappening.await() }

  def endTeach = lock.mutex { tuteHappening.signalAll() }
}
