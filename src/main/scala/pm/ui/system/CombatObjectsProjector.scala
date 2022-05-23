package pm.ui.system

import pm.model.{Actor, ActorId, CombatStarted, Coordinate, Event, World, PLAYER_ID}
import pm.system.{ActorDied, System}
import pm.system.MovementSystem.MovePerformed
import org.cosplay.*
import org.cosplay.{CPImage, CPImageSprite}
import CPColor.*
import CPArrayImage.*
import CPPixel.*
import pm.model.Material.Wall
import pm.ui.Game.WORLD_MARGIN

object CombatObjectsProjector extends System[World] {

  private type ObjectAction = CPSceneObjectContext => Unit

  private var actionsQueue: List[ObjectAction] = Nil

  def run(world: World, e: Event): (World, List[Event]) = e match
    case CombatStarted =>
      actionsQueue = actionsQueue :+ combatInitialization(world)
      (world, Nil)
    case MovePerformed(a: ActorId, c: Coordinate) =>
      actionsQueue = actionsQueue :+ actorMove(a, c)
      (world, Nil)
    case ActorDied(a: ActorId) =>
      actionsQueue = actionsQueue :+ actorRemoval(a)
      (world, Nil)
    case _ => (world, Nil)

  val sprite: CPOffScreenSprite = CPOffScreenSprite((ctx: CPSceneObjectContext) => {
    actionsQueue.foreach(a => a(ctx))
    actionsQueue = Nil
  })

  private def combatInitialization(world: World): ObjectAction = ctx => {
    for
      x <- 0 until world.locations.map.width
      y <- 0 until world.locations.map.height
    do
      world.locations.map.materialAt(Coordinate(x, y)) match
        case Wall => ctx.addObject(wallObject(Coordinate(x + WORLD_MARGIN, y + WORLD_MARGIN)))
        case _ => ()

    world.locations.actors
      .map((pos, id) => (pos, world.actors.get(id)))
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
