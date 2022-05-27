package pm.system

import monocle.*
import pm.model
import pm.model.*
import pm.system.*

object MovementHandler extends Handler1[LocationsState.type] :
  def handle(e: Event, s: LocationsState.type, locations: Locations): (List[Event],Locations)= e match
    case MoveAttempted(a, d) => locations.actorCoords(a) match
      case Some(c) =>
        if locations.passable(c.next(d))
        then locations.actorAt(c.next(d)) match
          case None => (List(MovePerformed(a, c.next(d))), locations.moveActor(a, c.next(d)))
          case Some(a) => (List(MoveBlockedByActor(a = a)), locations)
        else (List(MoveBlockedByEnvironment), locations)
      case None => (List(DebugEvent("Can't find an actor to move: " + a)), locations)

    // todo: przenieść do innego systemu?
    case ActorDied(actorId) => (Nil, locations.withoutActor(actorId))

    case _ => (Nil, locations)

case class MoveAttempted(a: ActorId, d: Direction) extends Event

case class MovePerformed(actorId: ActorId, newC: Coordinate) extends Action

object MoveBlockedByEnvironment extends Event

case class MoveBlockedByActor(a: ActorId) extends Event
