import ox.scl._

class Question3(n: Int) {
    private val putSem = new Semaphore(true)
    private val getSem = new Semaphore(false)

    private var array = new Array[Int](n)
    private var index = 0

    def put(item: Int) = {
        putSem.down
        array(index) = item
        index += 1
        if (index < n) putSem.up
        else getSem.up
    }

    def get : Array[Int] = {
        getSem.down
        val result = array
        array = new Array[Int](n)
        index = 0
        putSem.up
        result
    }
}
