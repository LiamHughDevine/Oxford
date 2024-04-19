import scala.collection.mutable.Stack

class TerminatingPartialStack[T](numWorkers: Int) {
  private val stack = new Stack[T]()
  private var waiters = 0
  private var closed = false

  def pop() : Option[T] = synchronized {
    if (!closed && stack.isEmpty) {
      if (waiters == numWorkers - 1) shutdown
      else {
        waiters += 1
        while (stack.isEmpty && !closed) wait()
        waiters -= 1
      }
    }
    if (closed) None
    else Some(stack.pop())
  }

  def push(x: T) = synchronized { if (!closed) { stack.push(x); notify() } }

  def shutdown = synchronized { closed = true; notifyAll()}
}