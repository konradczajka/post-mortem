package pm.game

import cats.data.State
import monocle.Lens
import pm.model
import pm.model.*
import pm.system.*
import pm.system.MovementSystem.MoveAttempted

import scala.util.Random

@main
def main: Unit =
  val w = createWorld()
  val p = createProgram()
  loop(w, p)


def loop(world: World, program: SystemAction): Unit =
  println(world.locations.print)
  Console.in.readLine() match
    case "q" => ()
    case c => toEvent(c) match
      case Some(e) => loop(runIteration(world, e, program), program)
      case _ => loop(world, program)

def toEvent(c: String): Option[Event] = c match
  case "w" => Some(MoveAttempted(PLAYER_ID, Direction.UP))
  case "s" => Some(MoveAttempted(PLAYER_ID, Direction.DOWN))
  case "a" => Some(MoveAttempted(PLAYER_ID, Direction.LEFT))
  case "d" => Some(MoveAttempted(PLAYER_ID, Direction.RIGHT))
  case "i" => Some(MeleeAttackAttempted(PLAYER_ID, Direction.UP))
  case "k" => Some(MeleeAttackAttempted(PLAYER_ID, Direction.DOWN))
  case "j" => Some(MeleeAttackAttempted(PLAYER_ID, Direction.LEFT))
  case "l" => Some(MeleeAttackAttempted(PLAYER_ID, Direction.RIGHT))
  case " " => Some(Wait(PLAYER_ID))
  case _ => None

def createWorld(): World =
  val player = Creature.player(hp = 10, atk = 4, acc=80, initiative = 8)
  val monster = Creature(id = "2", hp = 10, atk = 4, acc=50, ai = Some(TestMeleeEnemyAI), initiative = 6)
  val level: MapLevel = MapLevel.empty(20, 8)
  val actorsPositions = Map(
    Coordinate(2, 2) -> player.id,
    Coordinate(10, 6) -> monster.id)
  model.World(
    locations = Locations(map = level, actors = actorsPositions),
    events = CurrentEvents.empty,
    actors = Actors(Map(player.id -> player, monster.id -> monster)))

def createProgram(): SystemAction =

  val systems = List(
    EventLoggingSystem(Lens[World, Unit](_ => ())(_ => w => w)),
    MovementSystem(World.locationsL),
    CombatSystem(new Random(1))(World.locationsAndActorsL),
    ActorSystem(World.actorsL),
    AISystem(World.locationsAndActorsL),
    TurnsSystem(World.actorsL)
  )

  programFromSystems(systems)
