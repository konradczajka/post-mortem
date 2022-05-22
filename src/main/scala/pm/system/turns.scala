package pm.system

import pm.model.{Action, ActorId, Actors, Event, PLAYER_ID}

object TurnsSystem extends System[Actors]:
  def run(actors: Actors, e: Event): (Actors, List[Event]) = e match
    case ap: Action => (actors, List(TurnEnded(ap.actorId)))
    case TurnEnded(actorId) =>
      val queue = List.from(actors.actors.values).sortWith(_.initiative > _.initiative)
      val nextTurnIndex = queue.indexWhere(_.id == actorId) + 1
      val nextTurnActor = queue(nextTurnIndex % queue.size)
      (actors, List(TurnStarted(nextTurnActor.id)))
    case _ => (actors, Nil)

case class TurnEnded(actorId: ActorId) extends Event
case class TurnStarted(actorId: ActorId) extends Event
