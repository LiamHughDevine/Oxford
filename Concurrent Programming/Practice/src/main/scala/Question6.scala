import ox.scl._

class BagOfTasks(a: Double, b: Double, n: Int, nTasks: Int) {
    require (n%nTasks == 0)

    type Task = (Double, Double, Int, Double)

    private val delta = (b-a)/n
    private val taskSize = n/nTasks
    private val taskRange = (b-a)/nTasks
    private var left = a

    def getTask: Task = synchronized {
        if (left < b - (taskRange/2)) {
            val oldLeft = left; left = (left + taskRange) min b
            (oldLeft, left, taskSize, delta)
        }
        else throw new Stopped
    }
}

class Collector {
    private var result = 0.0

    def add (x: Double) = synchronized {
        result += x
    }

    def get : Double = synchronized { result }
}
