package pm.ui.system

import pm.system.*
import org.cosplay.*
import CPColor.*
import CPArrayImage.*
import CPPixel.*
import pm.model.*
import pm.model.Material.*
import pm.ui.Game.WORLD_MARGIN

object CombatObjectsProjector extends Handler2[LocationsState.type, ActorsState.type] {

  private type ObjectAction = CPSceneObjectContext => Unit

  private var actionsQueue: List[ObjectAction] = Nil


  def handle(e: Event, s1: LocationsState.type , s2: ActorsState.type , locations: Locations, actors: Actors): (List[Event], Locations, Actors) = e match
    case CombatStarted =>
      actionsQueue = actionsQueue :+ combatInitialization(locations, actors)
      (Nil, locations, actors)
    case MovePerformed(a: ActorId, c: Coordinate) =>
      actionsQueue = actionsQueue :+ actorMove(a, c)
      (Nil, locations, actors)
    case ActorDied(a: ActorId) =>
      actionsQueue = actionsQueue :+ actorRemoval(a)
      (Nil, locations, actors)
    case _ => (Nil, locations, actors)

  val sprite: CPOffScreenSprite = CPOffScreenSprite((ctx: CPSceneObjectContext) => {
    actionsQueue.foreach(a => a(ctx))
    actionsQueue = Nil
  })

  private def combatInitialization(locations: Locations, actors: Actors): ObjectAction = ctx => {
    for
      x <- 0 until locations.map.width
      y <- 0 until locations.map.height
    do
      locations.map.materialAt(Coordinate(x, y)) match
        case Wall => ctx.addObject(wallObject(Coordinate(x + WORLD_MARGIN, y + WORLD_MARGIN)))
        case _ => ()

    locations.actors
      .map((pos, id) => (pos, actors.get(id)))
      .foreach((pos, actorOpt) =>
        actorOpt match
          case Some(actor) => ctx.addObject(constructObject(pos, actor))
          case _           => ()
      )
  }

  private def actorRemoval: ActorId => ObjectAction = (a: ActorId) => _.deleteObject(a)

  private def actorMove: (ActorId, Coordinate) => ObjectAction = (a: ActorId, c: Coordinate) =>
    ctx =>
      ctx.getObject(a) match
        case Some(ac: ActorSprite) => ac.move(c.x + WORLD_MARGIN, c.y + WORLD_MARGIN)
        case _                     => ()

  private def constructObject(pos: Coordinate, actor: Actor): CPSceneObject =
    val sprite = actor.id match
      case PLAYER_ID => "@"
      case _         => "%"
    new ActorSprite(
      id = actor.id,
      x = pos.x + WORLD_MARGIN,
      y = pos.y + WORLD_MARGIN,
      img = new CPArrayImage(prepSeq(sprite), (ch, _, _) => ch & C_WHITE)
    )

  private def wallObject(pos: Coordinate): CPSceneObject =
    new ActorSprite(
      id = s"wall-${pos.x}-${pos.y}",
      x = pos.x,
      y = pos.y,
      img = new CPArrayImage(prepSeq("#"), (ch, _, _) => ch & C_WHITE)
    )
}

class ActorSprite(val id: String, var x: Int, var y: Int, img: CPImage)
    extends CPImageSprite(id = id, x = x, y = y, z = 1, img = img):
  def move(x: Integer, y: Integer): Unit =
    setX(x)
    setY(y)
