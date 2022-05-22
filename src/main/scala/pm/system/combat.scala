package pm.system

import monocle.Lens
import pm.model
import pm.model.*
import pm.system.MovementSystem.MoveAttempted

import scala.util.Random

case class CombatSystem(rng: Random) extends System[LocationsAndActors] :
  def run(ws: LocationsAndActors, e: Event): (LocationsAndActors, List[Event]) = e match
    case MeleeAttackAttempted(attackerId, d) => ws._1.actorCoords(attackerId) match
      case Some(attackerPos) => ws._1.actorAt(attackerPos.next(d)) match
        case Some(targetId) => ws._2.get(attackerId) match
          case Some(c: Creature) => if rng.between(1, 100) < c.acc then
            (ws, List(ActorHit(targetId, c.atk))) else
            (ws, List(Missed))
          case Some(_) => (ws, List(DebugEvent("Attacker is not a creature: " + attackerId)))
          case None => (ws, List(DebugEvent("Attacker not found : " + attackerId)))
        case None => (ws, List(NothingToAttack(attackerId)))
      case None => (ws, List(DebugEvent("Attacker not found on the map: " + attackerId)))

    case _ => (ws, Nil)

case class MeleeAttackAttempted(actorId: ActorId, d: Direction) extends Action

case class NothingToAttack(actorId: ActorId) extends Event
object Missed extends Event


@main
def testCombat: Unit =
  val player = Creature.player(hp = 10, atk = 6, acc=80, initiative = 8)
  val monster = Creature(id = "2", hp = 10, atk = 1, acc=100, ai = Some(TestMeleeEnemyAI), initiative = 6)
  val level: MapLevel = MapLevel.empty(10, 5)
  val actorsPositions = Map(
    Coordinate(2, 2) -> player.id,
    Coordinate(5, 2) -> monster.id)
  val initialWorld = model.World(
    locations = Locations(map = level, actors = actorsPositions),
    events = CurrentEvents.empty,
    actors = Actors(Map(player.id -> player, monster.id -> monster)))


  val events = List(
    MoveAttempted(player.id, Direction.RIGHT),
    MeleeAttackAttempted(player.id, Direction.LEFT),
    MeleeAttackAttempted(player.id, Direction.RIGHT),
    MeleeAttackAttempted(player.id, Direction.RIGHT),
//    MeleeAttackAttempted(player.id, Direction.RIGHT),
//    MeleeAttackAttempted(player.id, Direction.RIGHT),
//    MeleeAttackAttempted(player.id, Direction.RIGHT),
//    MoveAttempted(player.id, Direction.RIGHT)
  )

  val systems = List(
    EventLoggingSystem(Lens[World, Unit](_ => ())(_ => w => w)),
    MovementSystem(World.locationsL),
    CombatSystem(new Random(1))(World.locationsAndActorsL),
    ActorSystem(World.actorsL),
    AISystem(World.locationsAndActorsL),
    TurnsSystem(World.actorsL)
  )

  val p = programFromSystems(systems)

  val r = runEvents(p, initialWorld, events)

  println(r.locations.print)