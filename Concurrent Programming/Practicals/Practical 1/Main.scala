import ox.scl._
import ox.scl.channel.SyncChan
import math._

object Main {
  def main(args: Array[String]): Unit = {
    question5()
  }

  def question1() : Unit = {
    val a, b, c, d = new SyncChan[Int]
    var x, y = 0
    run (comparator(a, b, c, d) || thread {a!3} || thread {b!2} || thread {x=c?()} || thread {y=d?()})
    println(x)
    println(y)
  }

  def question2() : Unit = {
    val outs = List.tabulate(4)(_ => new SyncChan[Int])
    val ins = List.tabulate(4)(_ => new SyncChan[Int])
    val rand = new scala.util.Random
    val nums = List.tabulate(4)(_ => rand.between(0, 100))
    val sorted = new Array[Int](4)
    def p = || (for (i <- 0 until 4) yield {thread {outs(i)!nums(i)}})
    def q = || (for (i <- 0 until 4) yield {thread {sorted(i) = ins(i)?()}})
    run(sort4(outs, ins) || p || q)
    println("Original List")
    for (i <- 0 until 4) {
      println(nums(i))
    }
    println()
    println("Sorted List")
    for (i <- 0 until 4) {
      println(sorted(i))
    }
  }

  def question3() : Unit = {
    val n = 10
    val outs = List.tabulate(n)(_ => new SyncChan[Int])
    val out = new SyncChan[Int]
    val ins = List.tabulate(n+1)(_ => new SyncChan[Int])
    val rand = new scala.util.Random
    var nums = List.tabulate(n)(_ => rand.between(0, 100))
    nums = nums.sorted
    val num = rand.between(0, 100)
    val inserted = new Array[Int](n+1)

    def p = || (for (i <- 0 until n) yield {thread {outs(i)!nums(i)}})
    def q = || (for (i <- 0 until n + 1) yield {thread {inserted(i) = ins(i)?()}})

    //run (p || q || thread {out!num} || insert(outs, out, ins))
    run (p || q || thread {out!num} || binaryInsert(outs, out, ins))

    println("Original List:")
    for (i <- 0 until n) {
      println(nums(i))
    }
    println()
    println("To insert:")
    println(num)
    println()
    println("New List:")
    for (i <- 0 until n+1) {
      println(i + ": " + inserted(i))
    }
  }

  def question5() : Unit = {
    val n = 10
    val outs = List.tabulate(n)(_ => new SyncChan[Int])
    val ins = List.tabulate(n)(_ => new SyncChan[Int])
    val rand = new scala.util.Random
    val nums = List.tabulate(n)(_ => rand.between(0, 100))
    val sorted = new Array[Int](n)

    def p = || (for (i <- 0 until n) yield {thread {outs(i)!nums(i)}})
    def q = || (for (i <- 0 until n) yield {thread {sorted(i) = ins(i)?()}})

    run(insertionSort(outs, ins) || p || q)

    println("Original List:")
    for (i <- 0 until n) {
      println(i + ": " + nums(i))
    }
    println()
    println("Sorted List:")
    for (i <- 0 until n) {
      println(i + ": " + sorted(i))
    }
  }

  // A single comparator inputting on in0 and in1 and outputting on out0 (smaller value) and out1(larger value)
  def comparator(in0: ??[Int], in1: ??[Int], out0: !![Int], out1: !![Int]): ThreadGroup = {
    var x, y = 0
    val chan0, chan1 = new SyncChan[Unit]
    def p = thread {repeat(chan0?()); repeat(chan1?()); out0!min(x,y)}
    def q = thread {repeat(chan0?()); repeat(chan1?()); out1!max(x,y)}
    thread {x=in0?(); chan0.close} || thread {y=in1?(); chan1.close} || p || q
  }

  // A sorting network for four values
  def sort4(ins: List[??[Int]], outs: List[!![Int]]): ThreadGroup = {
    require(ins.length == 4 && outs.length == 4)
    val a, b, c, d, e, f = new SyncChan[Int]
    comparator(a, b, outs(0), e) || comparator(c, d, f, outs(3)) ||
      comparator(ins(0), ins(2), a, c) || comparator(ins(1), ins(3), b, d) ||
      comparator(e, f, outs(1), outs(2))
  }

  /* Insert a value input on in into a sorted sequence input on ins
  Pre: ins.length = n && outs.length = n + 1, for some n >= 1.
  If the values xs input on ins are sorted, and x is input on in,
  then a sorted permutation of x :: xs is output on ys. */

  def insert(ins: List[??[Int]], in: ??[Int], outs: List[!![Int]]): ThreadGroup = {
    val n = ins.length
    require(n >= 1 && outs.length == n + 1)
      if (n == 1) {
        comparator(in, ins(0), outs(0), outs(1))
      } else {
        val carry = new SyncChan[Int]
        comparator(in, ins(0), outs(0), carry) || insert(ins.tail, carry, outs.tail)
      }
  }

  def binaryInsert(ins: List[??[Int]], in: ??[Int], outs: List[!![Int]]): ThreadGroup = {
    val n = ins.length
    require(n >= 1 && outs.length == n + 1)

    if (n == 1) {
      comparator(in, ins(0), outs(0), outs(1))
    }
    else {
      val m = n / 2
      thread {
        val x = in?()
        val y = ins(m)?()
        val chan = new SyncChan[Int]
        if (x <= y) {
          run (binaryInsert(ins.take(m), chan, outs.take(m+1)) ||
            thread {chan!x} || copy(ins.drop(m+1), outs.drop(m+2)) || thread {outs(m+1)!y})
        }
        else {
          if (n == 2) {
            run (thread{outs(0)!ins(0)?()} || thread {outs(1)!y} || thread {outs(2)!x})
          }
          else {
            run (binaryInsert(ins.drop(m+1), chan, outs.drop(m+1)) ||
              thread {chan!x} || copy(ins.take(m), outs.take(m)) || thread {outs(m)!y})
          }
        }
      }
    }
  }



  def copy(ins: List[??[Int]], outs: List[!![Int]]): ThreadGroup = {
    val n = ins.length
    require(n >= 0 && outs.length == n)
    || (for (i <- 0 until n) yield {thread {outs(i)!ins(i)?()}})
  }

  /* Insertion sort.*/

  def insertionSort(ins: List[??[Int]], outs: List[!![Int]]): ThreadGroup = {
    val n = ins.length
    require(n >= 2 && outs.length == n)
    if (n == 2) {
      comparator(ins(0), ins(1), outs(0), outs(1))
    }
    else {
      val chans = List.tabulate(n-1)(_ => new BuffChan[Int](1))
      binaryInsert(chans, ins(0), outs) || insertionSort(ins.tail, chans)
    }
  }
}







/*def binaryInsert2(ins: List[??[Int]], in: ??[Int], outs: List[!![Int]]): ThreadGroup = {
    val n = ins.length
    require(n >= 1 && outs.length == n + 1)

    if (n == 1) {
      comparator(in, ins(0), outs(0), outs(1))
    }
    else {
      val m = n / 2
      var x, y = 0
      var wait0 = new SyncChan[Unit]
      var wait1 = new SyncChan[Unit]
      var wait2 = new SyncChan[Unit]
      var bins = Nil
      var bin = new SyncChan[Int]
      var bouts = Nil
      var cins = List.tabulate(n)(_ => new SyncChan[Int])
      var couts = List.tabulate(n)(_ => new SyncChan[Int])


      def p = thread {x = in?(); wait0!()}
      def q = thread {y = ins(m)?(); wait1!()}
      def r = {
        wait0?()
        wait1?()
        if (x <= y) {
          thread {bins = ins.take(m)} || thread {bouts = outs.take(m+1)} || thread{bin!x} ||
            thread {cins = ins.drop(m+1)} || thread {couts = outs.drop(m+2)}
        }
        else {
          if (n == 2) {
            thread {bins = ins.take(1)} || thread {bouts = outs.take(2)} || thread{bin!x} ||
              thread {cins = ins.drop(1)} || thread {couts = outs.drop(2)}
          }
          else {
            thread {bins = ins.drop(m+1)} || thread {bouts = outs.drop(m+1)} || thread{bin!x} ||
              thread {cins = ins.take(m)} || thread {couts = outs.take(m)}
          }
        }
      }

      p || q || r || thread {binaryInsert2(bins, bin, bouts)} || thread {copy(cins, couts)}
    }
  }*/