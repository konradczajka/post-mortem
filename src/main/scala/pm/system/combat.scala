package pm.system

import monocle.Lens
import pm.model
import pm.model.*
import pm.system.*

import scala.util.Random

case class CombatHandler(rng: Random) extends Handler2[LocationsState.type, ActorsState.type]:
  def handle(e: Event, s1: LocationsState.type , s2: ActorsState.type , locations: Locations, actors: Actors): (List[Event], Locations, Actors) = e match
    case MeleeAttackAttempted(attackerId, d) => locations.actorCoords(attackerId) match
      case Some(attackerPos) => locations.actorAt(attackerPos.next(d)) match
        case Some(targetId) => actors.get(attackerId) match
          case Some(c: Creature) => if rng.between(1, 100) < c.acc then
            (List(ActorHit(targetId, c.atk)), locations, actors) else
            (List(Missed), locations, actors)
          case Some(_) => (List(DebugEvent("Attacker is not a creature: " + attackerId)), locations, actors)
          case None => (List(DebugEvent("Attacker not found : " + attackerId)), locations, actors)
        case None => (List(NothingToAttack(attackerId)), locations, actors)
      case None => (List(DebugEvent("Attacker not found on the map: " + attackerId)), locations, actors)

    case _ => (Nil, locations, actors)

case class MeleeAttackAttempted(actorId: ActorId, d: Direction) extends Action

case class NothingToAttack(actorId: ActorId) extends Event
object Missed extends Event
