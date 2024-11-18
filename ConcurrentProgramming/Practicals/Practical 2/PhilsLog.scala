import ox.scl._
import scala.util.Random

/** Simulation of the Dining Philosophers example. */
class PhilsLog(number : Int) extends Philosophers {
  val N = number // Number of philosophers

  // Simulate basic actions
  def Eat = Thread.sleep(500)
  def Think = Thread.sleep(Random.nextInt(900))
  def Pause = Thread.sleep(500)

  // Each philosopher will send "pick" and "drop" commands to her forks, which
  // we simulate using the following values.
  type Command = Boolean
  val Pick = true; val Drop = false

  val log = new ox.scl.debug.SharedLog[LogEvent](N)

  private val shutdownChan = new SyncChan[Unit]

  /** A single left handed philosopher. */
  def philLeft(me: Int, left: !![Command], right: !![Command]) = thread("Phil"+me){
    repeat{
      Think
      log.add(me, new Sits(me)); Pause
      left!Pick; log.add(me, PickUp(me, left = true)); Pause
      right!Pick; log.add(me, PickUp(me, left = false)); Pause
      log.add(me, Eats(me)); Eat
      left!Drop; log.add(me, PutDown(me, left = true)); Pause; right!Drop; log.add(me, PutDown(me, left = false)); Pause
      log.add(me, Leaves(me))
      if(me == 0) print(".")
    }
  }

  /** A single right handed philosopher. */
  def philRight(me: Int, left: !![Command], right: !![Command]) = thread("Phil"+me){
    repeat{
      Think
      log.add(me, new Sits(me)); Pause
      right!Pick; log.add(me, PickUp(me, left = false)); Pause
      left!Pick; log.add(me, PickUp(me, left = true)); Pause
      log.add(me, Eats(me)); Eat
      right!Drop; log.add(me, PutDown(me, left = false)); Pause; left!Drop; log.add(me, PutDown(me, left = true)); Pause
      log.add(me, Leaves(me))
      if(me == 0) print(".")
    }
  }

  /** A single fork. */
  def fork(me: Int, left: ??[Command], right: ??[Command]) = thread("Fork"+me){
    serve(
      left =?=> {
        x => assert(x == Pick); println("Fork" + me); val y = left?(); assert(y == Drop)
      }
      |
      right =?=> {
        x => assert(x == Pick); println("Fork" + me); val y = right?(); assert(y == Drop)
      }
    )
  }

  /** The complete system. */
  val system = {
    // Channels to pick up and drop the forks:
    val philToLeftFork, philToRightFork = Array.fill(N)(new SyncChan[Command])
    // philToLeftFork(i) is from Phil(i) to Fork(i);
    // philToRightFork(i) is from Phil(i) to Fork((i-1)%N)
    val allPhils = || (
      for (i <- 0 until N)
      yield philLeft(i, philToLeftFork(i), philToRightFork(i))
    )
    val allForks = || (
      for (i <- 0 until N) yield
        fork(i, philToRightFork((i+1)%N), philToLeftFork(i))
    )
    allPhils || allForks || shutDownThread(philToLeftFork, philToRightFork)
  }

  /** Shuts down the system */
  def shutdown() = shutdownChan!()

  /** Thread to shut down all channels */

  def shutDownThread(leftForks : Array[SyncChan[Command]], rightForks : Array[SyncChan[Command]]) = thread ("Shutdown"){
    shutdownChan?()
    leftForks.foreach(x => x.close)
    rightForks.foreach(x => x.close)
  }

  /** Run the system. */
  def main(args : Array[String]) = {
    log.writeToFileOnShutdown("philsRightLog.txt")
    run(system)
  }

  def mainLog() : Array[LogEvent] = { log.writeToFileOnShutdown("philsRightLog.txt"); run(system) ; log.get }
}