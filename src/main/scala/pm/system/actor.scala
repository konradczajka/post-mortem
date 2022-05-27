package pm.system

import pm.model.*

object ActorHandler extends Handler1[ActorsState.type] :

  def handle(e: Event, s: ActorsState.type, actors: Actors): (List[Event],Actors)= e match
    case ActorHit(actorId, hpLoss) => actors.get(actorId) match
      case Some(c: Creature) =>
        val newC = c.copy(hp = c.hp - hpLoss)
        val events = if newC.hp > 0 then
          List(ActorLostHp(actorId, hpLoss)) else
          List(ActorLostHp(actorId, hpLoss), ActorDied(actorId))
        (events, actors.update(actorId, newC))
      case Some(_) => (List(DebugEvent("Unsupported actor hit : " + actorId)), actors)
      case None => (List(DebugEvent("Hit actor not found : " + actorId)), actors)

    case ActorDied(actorId) => (Nil, actors.remove(actorId))
    case _ => (Nil, actors)


case class Wait(actorId: ActorId) extends Action
case class ActorHit(a: ActorId, hpLoss: Int) extends Event

case class ActorLostHp(a: ActorId, hpLoss: Int) extends Event

case class ActorDied(a: ActorId) extends Event