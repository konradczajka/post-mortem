package pm.model

import pm.model.{Actor, ActorId, Locations}
import pm.system.CurrentEvents

trait StateType { type Type }

trait WorldState:
  def get(s: StateType): s.Type
  def put(s: StateType, v: s.Type): WorldState

case class InMemoryWorldState(private val states: Map[StateType, Any]) extends WorldState:
  def get(s: StateType): s.Type = states.get(s).map(_.asInstanceOf[s.Type]).getOrElse(throw new IllegalStateException(s"No state of type $s"))
  def put(s: StateType, v: s.Type): WorldState = InMemoryWorldState(states.updated(s, v))

// TODO "empty" dla states?
object LocationsState extends StateType {type Type = Locations}
object ActorsState extends StateType {type Type = Actors}
object CurrentEventsState extends StateType {type Type = CurrentEvents}
object UnitState extends StateType {type Type = Unit}
