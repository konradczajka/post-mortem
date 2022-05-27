package pm.model

import cats.implicits.*
import pm.system.*

trait AI:
  def process(actorId: ActorId, l: Locations, a: Actors): List[Event]

object TestMeleeEnemyAI2 extends AI :

  def process(actorId: ActorId, locations: Locations, actors: Actors): List[Event] =

    val result = for
      c1 <- locations.actorCoords(actorId)
      c2 <- locations.actorCoords(PLAYER_ID)
      distance <- c1.distanceTo(c2).some
      direction <- c1.directionTo(c2).some
    yield if distance > 1 then MoveAttempted(actorId, direction) else MeleeAttackAttempted(actorId, direction)

    result match
      case Some(event) => List(event)
      case None => List(DebugEvent(s"Can't determine an action for $actorId"))