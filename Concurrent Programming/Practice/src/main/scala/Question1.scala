import ox.scl._
import scala.collection.mutable

class Question1[T] {
    private def pushChan = new SyncChan[T]
    private def popChan = new SyncChan[Option[T]]

    server.fork
    private def server = thread {
        val stack = new mutable.Stack[T]
        serve (
            pushChan =?=> { x => stack.push(x) }
            | popChan =!=> { if (stack.nonEmpty) Some(stack.pop()) else None}
        )
    }

    def push(x: T) = pushChan!x

    def pop: Option[T] = popChan?()

    def shutdown = {pushChan.close; popChan.close}
}
