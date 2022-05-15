package pm.system

import monocle.*
import pm.model
import pm.model.*
import pm.system.MovementSystem.MoveAttempted

object MovementSystem extends System[Locations] :
  def run(locations: Locations, e: Event): (Locations, List[Event]) = e match
    case MoveAttempted(a, d) => locations.actorCoords(a) match
      case Some(c) =>
        if locations.passable(c.next(d))
        then locations.actorAt(c.next(d)) match
          case None => (locations.moveActor(a, c.next(d)), List(MovePerformed(a, c.next(d))))
          case Some(a) => (locations, List(MoveBlockedByActor(a = a)))
        else (locations, List(MoveBlockedByEnvironment))
      case None => (locations, List(DebugEvent("Can't find an actor to move: " + a)))

    // todo: przenieść do innego systemu?
    case ActorDied(actorId) => (locations.withoutActor(actorId), Nil)

    case _ => (locations, Nil)

  case class MoveAttempted(a: ActorId, d: Direction) extends Event

  case class MovePerformed(a: ActorId, newC: Coordinate) extends Event

  object MoveBlockedByEnvironment extends Event

  case class MoveBlockedByActor(a: ActorId) extends Event

@main
def testMov: Unit =
  val player = Creature.player(hp = 10, atk = 6)
  val otherId: ActorId = "2"
  val level: MapLevel = MapLevel.empty(10, 5)
  val actorsPositions = Map(Coordinate(2, 2) -> player.id)
  val initialWorld = model.World(locations = Locations(map = level, actors = actorsPositions), events = CurrentEvents.empty, actors = Actors(Map(player.id -> player)))


  val events = List(MoveAttempted(player.id, Direction.UP), MoveAttempted(player.id, Direction.RIGHT), MoveAttempted(otherId, Direction.UP), MoveAttempted(player.id, Direction.UP))
  val p = MovementSystem(World.locationsL).flatMap(_ => EventLoggingSystem(Lens[World, Unit](_ => ())(_ => w => w)))
  val r = events.foldLeft(initialWorld)((w, e) => runIteration(w, e, p))

  println(r.locations.print)