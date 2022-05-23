package pm.model

import cats.implicits.*
import pm.system.MovementSystem.MoveAttempted
import pm.system.{MeleeAttackAttempted, Wait}

trait AI:
  def process(actorId: ActorId, w: LocationsAndActors): List[Event]


object TestMeleeEnemyAI extends AI :
  def process(actorId: ActorId, w: LocationsAndActors): List[Event] =

    val result = for
      c1 <- w._1.actorCoords(actorId)
      c2 <- w._1.actorCoords(PLAYER_ID)
      distance <- c1.distanceTo(c2).some
      direction <- c1.directionTo(c2).some
    yield if distance > 1 then MoveAttempted(actorId, direction) else MeleeAttackAttempted(actorId, direction)

    result match
      case Some(event) => List(event)
      case None => List(DebugEvent(s"Can't determine an action for $actorId"))