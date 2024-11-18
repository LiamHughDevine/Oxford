import ox.scl._

class ConcGraphSearch[N](g: Graph[N]) extends GraphSearch[N](g){
  /**The number of workers. */
  val numWorkers = 8

  /** Perform a depth-first search in g, starting from start, for a node that
    * satisfies isTarget. */
  def apply(start: N, isTarget: N => Boolean): Option[N] = {
    val stack = new TerminatingPartialStack[N](numWorkers); stack.push(start)
    var found = false; var result: Option[N] = None

    def worker = thread("worker") {
      var done = false
      while (!done) {
        stack.pop() match {
          case Some(node) =>
            if (isTarget(node)) synchronized { if (!found) { found = true; result = Some(node); stack.shutdown }}
            else { g.succs(node).foreach(neighbour => stack.push(neighbour)) }
          case None => done = true
        }
        if (found) done = true
      }
    }

    run (|| (for (_ <- 0 until numWorkers) yield worker))
    result
  }
}