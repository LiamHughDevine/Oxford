import ox.scl._
import scala.collection.mutable

class Question5(n: Int) {
    private val mutex = new MutexSemaphore()
    private val addSem = new CountingSemaphore(n)
    private val removeSem = new CountingSemaphore(0)

    private val queue = new mutable.Queue[Int]()

    def enqueue(item: Int) = {
        addSem.down
        mutex.down
        queue.enqueue(item)
        removeSem.up
        mutex.up
    }

    def dequeue() : Int = {
        removeSem.down
        mutex.down
        val result = queue.dequeue()
        addSem.up
        mutex.up
        result
    }
}

// ---------------

import scala.collection.immutable.Queue
import ox.scl._

object BoundedBufferTest {
    var iters = 20
    val MaxVal = 200
    val numWorkers = 10 // MUST BE EVEN
    val n = 5

    type SeqBuffer = scala.collection.immutable.Queue[Int]
    type ConcBuffer = Question5

    def seqPut(x: Int)(buff: SeqBuffer): (Unit, SeqBuffer) = {
        require (buff.length < n)
        ((), buff.enqueue(x))
    }

    def seqGet(buff: SeqBuffer): (Int, SeqBuffer) = {
        require (buff.length != 0)
        buff.dequeue
    }

    def worker(me: Int, log: LinearizabilityLog[SeqBuffer, ConcBuffer]) = {
        val random = new scala.util.Random(scala.util.Random.nextInt() + me * 45207)
        if (me % 2 == 0) {
            for (i <- 0 until iters) {
                log ({ l => l.dequeue() }, "Get", seqGet)
            }
        }
        else {
            for (i <- 0 until iters) {
                val x = random.nextInt(MaxVal)
                log({ l => l.enqueue(x) }, "Put(" + x + ")", seqPut(x))
            }
        }
    }

    def main(args: Array[String]) = {
        var i = 0
        var reps = 10000
        while (i < args.length) args(i) match {
            case "--iters" => iters = args(i + 1).toInt; i += 2
            case "--reps" => reps = args(i + 1).toInt; i += 2
            case arg => println("Unrecognised argument: " + arg); sys.exit()
        }

        for (r <- 0 until reps) {
            val concBuffer = new ConcBuffer(n)
            val seqBuffer : SeqBuffer = Queue[Int]()
            val tester = LinearizabilityTester[SeqBuffer, ConcBuffer](seqBuffer, concBuffer, numWorkers, { (i, log) => worker(i, log) }) // seqQueue, concQueue, p, { (n:Int, log:LinearizabilityLog[SeqQueue, ConcQueue]) => worker(n,log) })
            assert(tester() > 0)
            if (r % 20 == 0) print(".")
        }
        println()
    }
}