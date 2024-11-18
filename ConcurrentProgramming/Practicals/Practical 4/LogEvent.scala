abstract class LogEvent
case class StudentArrives(me: Int) extends LogEvent
case class TuteStarts() extends LogEvent
case class TuteEnds() extends LogEvent
case class StudentLeaves(me: Int) extends LogEvent