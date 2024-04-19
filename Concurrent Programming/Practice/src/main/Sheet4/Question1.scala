import ox.scl._

class Question1(n: Int) {
    private val lock = new Lock
    private val canPut, canGet = lock.newCondition

    private var array = new Array[Int](n)
    private var index = 0

    def put(item: Int) = lock.mutex {
        canPut.await(index < n)
        array(index) = item
        index += 1
        if (index == n) canGet.signal()
    }

    def get : Array[Int] = lock.mutex {
        if (index < n) canGet.await()
        val result = array
        array = new Array[Int](n)
        index = 0
        canPut.signalAll()
        result
    }
}
