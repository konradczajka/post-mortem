package pm.ui.system

import pm.model.{Actor, ActorId, CombatStarted, Coordinate, Event, World, PLAYER_ID}
import pm.system.{ActorDied, System}
import pm.system.MovementSystem.MovePerformed
import org.cosplay.*
import org.cosplay.{CPImage, CPImageSprite}
import CPColor.*
import CPArrayImage.*
import CPPixel.*

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
        case Some(ac: ActorSprite) => ac.move(c.x, c.y)
        case _                     => ()

  private def constructObject(pos: Coordinate, actor: Actor): CPSceneObject =
    val sprite = actor.id match
      case PLAYER_ID => "@"
      case _         => "%"
    new ActorSprite(
      id = actor.id,
      x = pos.x,
      y = pos.y,
      img = new CPArrayImage(prepSeq(sprite), (ch, _, _) => ch & C_WHITE)
    )
}

class ActorSprite(val id: String, var x: Int, var y: Int, img: CPImage)
    extends CPImageSprite(id = id, x = x, y = y, z = 1, img = img):
  def move(x: Integer, y: Integer): Unit =
    setX(x)
    setY(y)
