package pm.system

import pm.model.*
import pm.system.*

object TurnsHandler extends Handler1[ActorsState.type]:

  override def handle(e: Event, s: ActorsState.type , actors: Actors): (List[Event], Actors) = e match
    case ap: Action => (List(TurnEnded(ap.actorId)), actors)
    case TurnEnded(actorId) =>
      val queue = List.from(actors.actors.values).sortWith(_.initiative > _.initiative)
      val nextTurnIndex = queue.indexWhere(_.id == actorId) + 1
      val nextTurnActor = queue(nextTurnIndex % queue.size)
      (List(TurnStarted(nextTurnActor.id)), actors)
    case _ => (Nil, actors)

case class TurnEnded(actorId: ActorId) extends Event
case class TurnStarted(actorId: ActorId) extends Event
