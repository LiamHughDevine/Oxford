import ox.scl._
import ox.scl.channel.SyncChan
import math._

object Question1 {
  def main(args: Array[String]): Unit = {
    val a, b, c, d = new SyncChan[Int]
    val comp = comparator(a, b, c, d)
    var x, y = 0
    run ( comp || thread {a!1} || thread {b!(2)} || thread {x=c?()} || thread {y=d?()})
    println(x)
    println(y)
  }

  /* A single comparator
  , inputting on in0 and in1
  , and outputting on out0 (smaller value) and out1(larger value)*/

  def comparator(in0: ??[Int], in1: ??[Int], out0: !![Int], out1: !![Int]): ThreadGroup = thread("comparator") {
    var x, y = 0
    run ( thread { x = in0?()} || thread { y = in1?()})
    run ( thread { out0!min(x, y)} || thread { out1!max(x, y)})
  }
}


