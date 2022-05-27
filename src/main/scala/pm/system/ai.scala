package pm.system

import pm.model.*
import pm.system.*

object AIHandler extends Handler2[LocationsState.type, ActorsState.type] :

  def handle(e: Event, s1: LocationsState.type, s2: ActorsState.type, locations: Locations, actors: Actors): (List[Event], Locations, Actors) = e match
    case TurnStarted(actorId) => actors.get(actorId) match
      case Some(Creature(_, _, _, _,Some(ai), _)) => (ai.process(actorId, locations, actors), locations, actors)
      case _ => (Nil, locations, actors)
    case _ => (Nil, locations, actors)