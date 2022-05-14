package pm.system

trait Event

case class DebugEvent(msg: String) extends Event