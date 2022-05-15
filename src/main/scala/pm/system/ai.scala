package pm.system

import pm.model.{ActorId, Creature, LocationsAndActors}

object AISystem extends System[LocationsAndActors]:
  def run(ws: LocationsAndActors, e: Event): (LocationsAndActors, List[Event]) = e match
    case TurnStarted(actorId) => ws._2.get(actorId) match
      case Some(Creature(_, _, _, Some(ai))) => (ws, ai.process(actorId, ws))
      case _ => (ws, Nil)
    case _ => (ws, Nil)


// TODO: przenieść do systemu tur
case class TurnStarted(actorId: ActorId) extends Event