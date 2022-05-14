package pm.model

import monocle.Lens
import monocle.macros.GenLens
import pm.model.{Actor, ActorId, Locations, World}
import pm.system.{CurrentEvents, Event}

type LocationsAndNpcs = (Locations, Map[ActorId, Actor])

// TODO: builder
case class World(events: CurrentEvents,
                 locations: Locations,
                 actors: Map[ActorId, Actor])

object World:
  def collectedEventsL: Lens[World, List[Event]] = GenLens[World](_.events.collected)

  def currentEventL: Lens[World, Option[Event]] = GenLens[World](_.events.current)

  def actorsL: Lens[World, Map[ActorId, Actor]] = GenLens[World](_.actors)
  def locationsL: Lens[World, Locations] = GenLens[World](_.locations)

  def locationsAndNpcsL: Lens[World, LocationsAndNpcs] = Lens[World, LocationsAndNpcs](w => (w.locations, w.actors))((ls, as) => w => w.copy(locations = ls, actors = as))
