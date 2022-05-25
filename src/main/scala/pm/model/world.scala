package pm.model

import monocle.Lens
import monocle.macros.GenLens
import pm.model.{Actor, ActorId, Locations, World}
import pm.system.CurrentEvents

type LocationsAndActors = (Locations, Actors)
type InventoryAndActors = (Inventory, Actors)

// TODO: builder
case class World(events: CurrentEvents,
                 locations: Locations,
                 actors: Actors,
                 inventory: Inventory = Inventory(Map()))

trait WorldLens[A]:
  def lens: Lens[World, A]

object World:

  given WorldLens[List[Event]] with
    def lens = collectedEventsL

  given WorldLens[World] with
    def lens = worldL

  given WorldLens[Unit] with
    def lens = unitL

  given WorldLens[Option[Event]] with
    def lens = currentEventL

  given WorldLens[Actors] with
    def lens = actorsL

  given WorldLens[Locations] with
    def lens = locationsL

  given WorldLens[LocationsAndActors] with
    def lens = locationsAndActorsL

  given WorldLens[InventoryAndActors] with
    def lens = inventoryAndActorsL

  given WorldLens[Inventory] with
    def lens = inventoryL

  def worldL: Lens[World, World] = Lens[World, World](w => w)(w => _ => w)
  def unitL: Lens[World, Unit] = Lens[World, Unit](_ => ())(_ => w => w)
  def collectedEventsL: Lens[World, List[Event]] = GenLens[World](_.events.collected)
  def currentEventL: Lens[World, Option[Event]] = GenLens[World](_.events.current)

  def actorsL: Lens[World, Actors] = GenLens[World](_.actors)
  def locationsL: Lens[World, Locations] = GenLens[World](_.locations)
  def inventoryL: Lens[World, Inventory] = GenLens[World](_.inventory)

  def locationsAndActorsL: Lens[World, LocationsAndActors] = Lens[World, LocationsAndActors](w => (w.locations, w.actors))((ls, as) => w => w.copy(locations = ls, actors = as))
  def inventoryAndActorsL: Lens[World, InventoryAndActors] = Lens[World, InventoryAndActors](w => (w.inventory, w.actors))((is, as) => w => w.copy(inventory = is, actors = as))
