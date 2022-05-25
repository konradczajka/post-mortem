package pm.model

import scala.collection.immutable.Map

case class Inventory(actorsItems: Map[ActorId, List[Item[_]]])
