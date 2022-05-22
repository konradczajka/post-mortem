package pm.model

import monocle.Lens
import monocle.macros.GenLens
import pm.model.{Actor, ActorId, Locations, World}
import pm.system.CurrentEvents

type LocationsAndActors = (Locations, Actors)

// TODO: builder
case class World(events: CurrentEvents,
                 locations: Locations,
                 actors: Actors)

object World:
  def collectedEventsL: Lens[World, List[Event]] = GenLens[World](_.events.collected)

  def currentEventL: Lens[World, Option[Event]] = GenLens[World](_.events.current)

  def actorsL: Lens[World, Actors] = GenLens[World](_.actors)
  def locationsL: Lens[World, Locations] = GenLens[World](_.locations)

  def locationsAndActorsL: Lens[World, LocationsAndActors] = Lens[World, LocationsAndActors](w => (w.locations, w.actors))((ls, as) => w => w.copy(locations = ls, actors = as))
