import ox.scl._
import scala.util.Random

/** Simulation of the Dining Philosophers example.
 * Design Consideration: One channel per philosopher to the butler*/
class PhilsButlerLog(number : Int) extends Philosophers {
  val N = number // Number of philosophers

  // Simulate basic actions
  def Eat = Thread.sleep(50)
  def Think = Thread.sleep(Random.nextInt(90))
  def Pause = Thread.sleep(50)

  // Each philosopher will send "pick" and "drop" commands to her forks, which
  // we simulate using the following values.
  type Command = Boolean
  val Pick = true; val Drop = false
  val Sit = true; val Stand = false

  val log = new ox.scl.debug.SharedLog[LogEvent](N)

  private val shutdownChan = new SyncChan[Unit]

  /** A single left handed philosopher. */
  def phil(me: Int, left: !![Command], right: !![Command], butler: !![Command]) = thread("Phil"+me){
    repeat{
      Think
      butler!Sit
      log.add(me, new Sits(me)); Pause
      left!Pick; log.add(me, PickUp(me, left = true)); Pause
      right!Pick; log.add(me, PickUp(me, left = false)); Pause
      log.add(me, Eats(me)); Eat
      left!Drop; Pause; right!Drop; Pause
      butler!Stand
      log.add(me, Leaves(me))
      if(me == 0) print(".")
    }
  }

  /** A single fork. */
  def fork(me: Int, left: ??[Command], right: ??[Command]) = thread("Fork"+me){
    serve(
      left =?=> {
        x => assert(x == Pick); val y = left?(); assert(y == Drop); log.add(me + N, PutDown((me + 1) % N, left = false))
      }
      |
      right =?=> {
        x => assert(x == Pick); val y = right?(); assert(y == Drop); log.add(me + N, PutDown(me, left = true))
      }
    )
  }

  /** The butler */
  def butler(phils : Array[SyncChan[Command]]) = thread("Butler"){
    var seated = 0
    val philSeated = Array.fill(N)(false)
    serve (
      | (for (i <- 0 until N) yield
      (seated < N - 1 || philSeated(i)) && phils(i) =?=> { command =>
        if (philSeated(i)) {
          assert(command == Stand)
          seated -= 1
          philSeated(i) = false
        } else {
          assert(command == Sit)
          seated += 1
          philSeated(i) = true
        }
      })
    )
  }

  /** The complete system. */
  val system = {
    // Channels to pick up and drop the forks:
    val philToLeftFork, philToRightFork = Array.fill(N)(new SyncChan[Command])
    // philToLeftFork(i) is from Phil(i) to Fork(i);
    // philToRightFork(i) is from Phil(i) to Fork((i-1)%N)

    // Channels to communicate with the butler:
    val philToButler = Array.fill(N)(new SyncChan[Command])
    val allPhils = || (
      for (i <- 0 until N)
      yield phil(i, philToLeftFork(i), philToRightFork(i), philToButler(i))
    )
    val allForks = || (
      for (i <- 0 until N) yield
        fork(i, philToRightFork((i+1)%N), philToLeftFork(i))
    )
    allPhils || allForks || butler(philToButler) || shutDownThread(philToLeftFork, philToRightFork, philToButler)
  }

  /** Shuts down the system */
  def shutdown() = shutdownChan!()

  /** Thread to shut down all channels */

  def shutDownThread(leftForks : Array[SyncChan[Command]], rightForks : Array[SyncChan[Command]], butlers : Array[SyncChan[Command]]) = thread ("Shutdown"){
    shutdownChan?()
    leftForks.foreach(x => x.close)
    rightForks.foreach(x => x.close)
    butlers.foreach(x => x.close)
  }

  /** Run the system. */
  def main(args : Array[String]) = {
    log.writeToFileOnShutdown("philsButler.txt")
    run(system)
  }

  def mainLog() : Array[LogEvent] = { log.writeToFileOnShutdown("philsButler.txt"); run(system) ; log.get }
}