import ox.scl._

class SleepingTutorSemaphore extends SleepingTutor {
  private val tutorArrive = new MutexSemaphore
  private val studentArrive = new SignallingSemaphore
  private val tutorStart = new SignallingSemaphore
  private val studentLeave = new SignallingSemaphore

  // Checks whether the other student has already either arrived or left
  private var firstStudentResolved = false

  def tutorWait = { tutorArrive.down; studentArrive.up; tutorStart.down }

  def arrive = {
    studentArrive.down
    if (!firstStudentResolved) { firstStudentResolved = true; studentArrive.up }
    else { firstStudentResolved = false; tutorStart.up }
  }

  def receiveTute = {
    studentLeave.down
    if (!firstStudentResolved) { firstStudentResolved = true; studentLeave.up }
    else { firstStudentResolved = false; tutorArrive.up }
  }

  def endTeach = studentLeave.up
}
