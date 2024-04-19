import ox.scl._

object Main {
  def main(args: Array[String]): Unit = {
    timeOut()
  }

  /** Runs tests for the right handed implementation */
  def rightHanded(): Unit = {
    for (i <- 5 until 10) {
      println("Testing Right Handed for N = " + i)
      val phil = new PhilsRightLog(i)
      runTest(phil, i)
      println("Success")
    }
  }

  /** Runs tests for the butler implementation */
  def butler(): Unit = {
    for (i <- 5 until 6) {
      println("Testing Butler for N = " + i)
      val phil = new PhilsButlerLog(i)
      runTest(phil, i)
      println("Success")
    }
  }

  /** Runs tests for the time out implementation */
  def timeOut(): Unit = {
    for (i <- 5 until 10) {
      println("Testing Time Out for N = " + i)
      val phil = new PhilsTimeOutLog(i)
      runTest(phil, i)
      println("Success")
    }
  }

  /** Lets the process run for 10 seconds, then stops it and tests that it is correct */
  def runTest(phil: Philosophers, N: Int) = {
    var log = Array[LogEvent]()
    def p = thread { log = phil.mainLog() }
    def q = thread { Thread.sleep(10000); phil.shutdown()}
    run (p || q)
    if(!checkLog(log, N)) { println("Test Failed"); sys.exit() }
  }

  /** Verifies that the log is correct
   *
   * Design considerations for the log:
   * Put all of the logging into the philosopher except for putting down the fork as it is important that
   * the fork is logged as being put down before another philosopher can pick it up*/
  def checkLog(events : Array[LogEvent], N : Int) : Boolean = {
    val forks = Array.fill(N)(forkStatus.table)
    val phils = Array.fill(N)(philStatus.stand)

    // Checks for deadlocks
    if (events.length < 30) return false

    // Simulates the philosophers
    for (event <- events) {
      event match {
        case Sits(phil) =>
          if (phils(phil) == philStatus.sit) return false
          phils(phil) = philStatus.sit
        case PickUp(phil, left) =>
          if (phils(phil) != philStatus.sit) return false
          if (!left) {
            if (forks(phil) != forkStatus.table) return false
            forks(phil) = forkStatus.rightPhil
          }
          else {
            if (forks((phil + 1) % N) != forkStatus.table) return false
            forks((phil + 1) % N) = forkStatus.leftPhil
          }
        case Eats(phil) =>
          if (phils(phil) != philStatus.sit) return false
          if (forks(phil) != forkStatus.rightPhil) return false
          if (forks((phil + 1) % N) != forkStatus.leftPhil) return false
        case PutDown(phil, left) =>
          if (phils(phil) != philStatus.sit) return false
          if (!left) {
            if (forks(phil) != forkStatus.rightPhil) return false
            forks(phil) = forkStatus.table
          } else {
            if (forks((phil + 1) % N) != forkStatus.leftPhil) return false
            forks((phil + 1) % N) = forkStatus.table
          }
        case Leaves(phil) =>
          if (phils(phil) == philStatus.stand) return false
          phils(phil) = philStatus.stand
      }
    }
    true
  }
}

/** Represents where a fork is */
object forkStatus extends Enumeration {
  type forkStatus = Value
  val leftPhil, rightPhil, table = Value
}

/** Represents where a philosopher is */
object philStatus extends Enumeration {
  type philStatus = Value
  val stand, sit = Value
}