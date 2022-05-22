package pm.system

import pm.model.{ActorId, Creature, Event, LocationsAndActors}

object AISystem extends System[LocationsAndActors]:
  def run(ws: LocationsAndActors, e: Event): (LocationsAndActors, List[Event]) = e match
    case TurnStarted(actorId) => ws._2.get(actorId) match
      case Some(Creature(_, _, _, _,Some(ai), _)) => (ws, ai.process(actorId, ws))
      case _ => (ws, Nil)
    case _ => (ws, Nil)
