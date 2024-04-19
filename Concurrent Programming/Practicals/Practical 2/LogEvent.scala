abstract class LogEvent
case class Sits(phil : Int) extends LogEvent
case class PickUp(phil : Int, left : Boolean) extends LogEvent
case class PutDown(phil : Int, left : Boolean) extends LogEvent
case class Eats(phil: Int) extends LogEvent
case class Leaves(phil: Int) extends LogEvent