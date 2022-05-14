package pm.system

import pm.model.*

object ActorSystem extends System[Map[ActorId, Actor]] :
  def run(actors: Map[ActorId, Actor], e: Event): (Map[ActorId, Actor], List[Event]) = e match
    case ActorHit(actorId, hpLoss) => actors.get(actorId) match
      case Some(c: Creature) =>
        val newC = c.copy(hp = c.hp - hpLoss)
        val events = if newC.hp >= 0 then
          List(ActorLostHp(actorId, hpLoss)) else
          List(ActorLostHp(actorId, hpLoss), ActorDied(actorId))
        (actors.updated(actorId, newC), events)
      case Some(_) => (actors, List(DebugEvent("Unsupported actor hit : " + actorId)))
      case None => (actors, List(DebugEvent("Hit actor not found : " + actorId)))

    case ActorDied(actorId) => (actors - actorId, Nil)

    case _ => (actors, Nil)


case class ActorHit(a: ActorId, hpLoss: Int) extends Event

case class ActorLostHp(a: ActorId, hpLoss: Int) extends Event

case class ActorDied(a: ActorId) extends Event