package pm.ui

import monocle.Lens
import org.cosplay.{CPDim, CPEngine, CPGameInfo}
import pm.model.*
import pm.system.*
import pm.ui.system.*

import scala.util.Random

object Game:
  val WORLD_MARGIN = 5
  var w = createWorld()
  val p = createProgram()

  def main(args: Array[String]): Unit =

    CPEngine.init(
      CPGameInfo(name = "Post mortem"),
      System.console() == null || args.contains("emuterm")
    )

    val dim = CPDim(40 + WORLD_MARGIN * 2, 20 + WORLD_MARGIN * 2)
    val sc = new CombatScene(dim)


    try CPEngine.startGame(sc)
    finally CPEngine.dispose()

    sys.exit(0)

def createWorld(): WorldState =
  val player = Creature.player(hp = 10, atk = 4, acc = 80, initiative = 8)
  val monster = Creature(id = "actor-2", hp = 10, atk = 4, acc = 50, ai = Some(TestMeleeEnemyAI2), initiative = 6)
  val level: MapLevel = MapLevel.empty(40, 20)
  val actorsPositions = Map(
    Coordinate(2, 2) -> player.id,
    Coordinate(10, 6) -> monster.id)
  InMemoryWorldState(Map())
    .put(LocationsState, Locations(map = level, actors = actorsPositions))
    .put(CurrentEventsState, CurrentEvents(None, Nil))
    .put(UnitState, ())
    .put(ActorsState, Actors(Map(player.id -> player, monster.id -> monster)))

def createProgram(): WorldState => WorldState =

  val systems: List[WorldState => WorldState] = List(
    EventLoggingHandler(UnitState),
    MovementHandler(LocationsState),
    CombatHandler(new Random(1))(LocationsState, ActorsState),
    ActorHandler(ActorsState),
    AIHandler(LocationsState, ActorsState),
    TurnsHandler(ActorsState),
    CombatObjectsProjector(LocationsState, ActorsState)
  )

  buildProgram(systems)