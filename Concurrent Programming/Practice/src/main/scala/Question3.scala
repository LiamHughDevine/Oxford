import ox.scl._

class Question3(n: Int, a: Array[Int]) {
    require(n == a.size)

    private val barrier = new Barrier(n)

    private val sum = new Array[Int](n)
    private val toSummers = new Array[Int](n)

    private def summer(me: Int) = thread {
        var gap = 1; var s = a(me)
        while (gap < n) {
            if (me + gap < n) toSummers(me+gap) = s
            barrier.sync(me)
            if (gap <= me) s += toSummers(me)
            gap += gap
            barrier.sync(me)
        }
        sum(me) = s
    }

    def apply() : Array[Int] = {
        run (|| (for (i <- 0 until n) yield summer(i)))
        sum
    }
}
