package pm.model

import pm.system.{System, SystemAction}

trait Item[A] {
  def system: System[A]
}

case class ItemEquipped(actorId: ActorId, item: Item[_]) extends Event
case class ItemRemoved(actorId: ActorId, item: Item[_]) extends Event

object AtkAmu1 extends Item[Actors]:
  def system: System[Actors] = (actors: Actors, e: Event) => e match
    case ItemEquipped(actorId, _) => actors.get(actorId) match
      case Some(actor: Creature) => (actors.update(actorId, actor.copy(atk = actor.atk + 1)), Nil)
      case Some(_) => (actors, List(DebugEvent(s"Non-creature actor with id $actorId can't equip $AtkAmu1")))
      case None => (actors, List(DebugEvent(s"Can't find the actor with id $actorId to equip $AtkAmu1")))
    case ItemRemoved(actorId, _) => actors.get(actorId) match
      case Some(actor: Creature) => (actors.update(actorId, actor.copy(atk = actor.atk - 1)), Nil)
      case Some(_) => (actors, List(DebugEvent(s"Non-creature actor with id $actorId can't remove $AtkAmu1")))
      case None => (actors, List(DebugEvent(s"Can't find the actor with id $actorId to remove $AtkAmu1")))
    case _ => (actors, Nil)