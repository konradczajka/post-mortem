package pm.system

import monocle.Lens
import pm.model
import pm.model.*
import pm.system.MovementSystem.MoveAttempted

object CombatSystem extends System[LocationsAndActors] :
  def run(ws: LocationsAndActors, e: Event): (LocationsAndActors, List[Event]) = e match
    case MeleeAttackAttempted(attackerId, d) => ws._1.actorCoords(attackerId) match
      case Some(attackerPos) => ws._1.actorAt(attackerPos.next(d)) match
        case Some(targetId) => ws._2.get(attackerId) match
          case Some(c: Creature) => (ws, List(ActorHit(targetId, c.atk)))
          case Some(_) => (ws, List(DebugEvent("Attacker is not a creature: " + attackerId)))
          case None => (ws, List(DebugEvent("Attacker not found : " + attackerId)))
        case None => (ws, List(NothingToAttack))
      case None => (ws, List(DebugEvent("Attacker not found on the map: " + attackerId)))

    case _ => (ws, Nil)

case class MeleeAttackAttempted(attacker: ActorId, d: Direction) extends Event

object NothingToAttack extends Event


@main
def testCombat: Unit =
  val player = Creature.player(hp = 10, atk = 6)
  val monster = Creature(id = "2", hp = 10, atk = 1, ai = Some(TestMeleeEnemyAI))
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
    TurnStarted(monster.id),
    MeleeAttackAttempted(player.id, Direction.LEFT),
    TurnStarted(monster.id),
    MeleeAttackAttempted(player.id, Direction.RIGHT),
    TurnStarted(monster.id),
    MeleeAttackAttempted(player.id, Direction.RIGHT),
//    MeleeAttackAttempted(player.id, Direction.RIGHT),
//    MeleeAttackAttempted(player.id, Direction.RIGHT),
//    MeleeAttackAttempted(player.id, Direction.RIGHT),
//    MoveAttempted(player.id, Direction.RIGHT)
  )

  val systems = List(
    EventLoggingSystem(Lens[World, Unit](_ => ())(_ => w => w)),
    MovementSystem(World.locationsL),
    CombatSystem(World.locationsAndActorsL),
    ActorSystem(World.actorsL),
    AISystem(World.locationsAndActorsL)
  )

  val p = programFromSystems(systems)

  val r = runEvents(p, initialWorld, events)

  println(r.locations.print)