package pm.system

import monocle.Lens
import pm.model
import pm.model.*
import pm.model.World.given
import pm.system.MovementSystem.MoveAttempted

import scala.util.Random

//case class ItemsSystem() extends System[Unit] :
//  def run(ws: Unit, e: Event): (Unit, List[Event]) = e match
//    case ItemEquipped(actorId, item) =>
//    case ItemRemoved(actorId, item) => 
//    case _ => (ws, Nil)
