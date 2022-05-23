package pm.ui

import monocle.Lens
import pm.ui.system.*
import org.cosplay.*
import pm.model.*
import pm.system.*
import pm.system.MovementSystem.*

import scala.util.Random

object Game:
  var w = createWorld()
  val p = createProgram()
  def main(args: Array[String]): Unit =


    CPEngine.init(
      CPGameInfo(name = "Post mortem"),
      System.console() == null || args.contains("emuterm")
    )

    val dim = CPDim(100, 50)
    val sc = new CombatScene(dim)


    try CPEngine.startGame(sc)
    finally CPEngine.dispose()

    sys.exit(0)



def createWorld(): World =
  val player = Creature.player(hp = 10, atk = 4, acc=80, initiative = 8)
  val monster = Creature(id = "actor-2", hp = 10, atk = 4, acc=50, ai = Some(TestMeleeEnemyAI), initiative = 6)
  val level: MapLevel = MapLevel.empty(100, 50)
  val actorsPositions = Map(
    Coordinate(2, 2) -> player.id,
    Coordinate(10, 6) -> monster.id)
  World(
    locations = Locations(map = level, actors = actorsPositions),
    events = CurrentEvents.empty,
    actors = Actors(Map(player.id -> player, monster.id -> monster)))

def createProgram(): SystemAction =

  val systems: List[SystemAction] = List(
    EventLoggingSystem(Lens[World, Unit](_ => ())(_ => w => w)),
    MovementSystem(World.locationsL),
    CombatSystem(new Random(1))(World.locationsAndActorsL),
    ActorSystem(World.actorsL),
    AISystem(World.locationsAndActorsL),
    TurnsSystem(World.actorsL),
    CombatObjectsProjector(Lens[World, World](w => w)(_ => w => w))
  )

  programFromSystems(systems)