package pm.model

trait Event

trait Action extends Event:
  def actorId: ActorId

case class DebugEvent(msg: String) extends Event