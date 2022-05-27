package pm.system

import cats.*
import cats.data.State
import cats.data.State.*
import cats.implicits.*
import monocle.*
import monocle.macros.GenLens
import monocle.syntax.all.*
import pm.model.*

import scala.annotation.tailrec

// TODO: Monoid?
case class CurrentEvents(current: Option[Event], collected: List[Event])

object CurrentEvents:
  def empty: CurrentEvents = CurrentEvents(None, List())

object EventLoggingHandler extends Handler1[UnitState.type]:
  def handle(e: Event, s: UnitState.type, v: Unit): (List[Event], Unit) = {
    println(e)
    (Nil, ())
  }

trait Handler1[S <: StateType]:
  def handle(e: Event, s: S, v: s.Type): (List[Event], s.Type)

  final def apply(s: S): WorldState => WorldState = (ws: WorldState) =>
    val ce = ws.get(CurrentEventsState)

    ce.current match
      case Some(event) =>
        val (events, newState) = handle(event, s, ws.get(s))
        ws.put(CurrentEventsState, CurrentEvents(ce.current, ce.collected ::: events))
          .put(s, newState)
      case None => ws

trait Handler2[S1 <: StateType, S2 <: StateType]:
  def handle(e: Event, s1: S1, s2: S2, v1: s1.Type, v2: s2.Type): (List[Event], s1.Type, s2.Type)

  final def apply(s1: S1, s2: S2): WorldState => WorldState = (ws: WorldState) =>
    val ce = ws.get(CurrentEventsState)

    ce.current match
      case Some(event) =>
        val (events, nl, na) = handle(event, s1, s2, ws.get(s1), ws.get(s2))
        ws.put(CurrentEventsState, CurrentEvents(ce.current, ce.collected ::: events))
          .put(s1, nl)
          .put(s2, na)
      case None => ws

def buildProgram(handlers: List[WorldState => WorldState]): WorldState => WorldState =
  handlers.foldLeft((w => w): WorldState => WorldState)((s1, s2) => s1 andThen s2)

@tailrec
def runIteration(ws: WorldState, e: Event, p: WorldState => WorldState): WorldState =
  val ce = ws.get(CurrentEventsState)
  val nws = p(ws.put(CurrentEventsState, CurrentEvents(Some(e), ce.collected)))
  val nce = nws.get(CurrentEventsState)
  nce.collected match
    case e :: rest =>
      runIteration(nws.put(CurrentEventsState, CurrentEvents(Some(e), rest)), e, p)
    case Nil => nws.put(CurrentEventsState, CurrentEvents(None, Nil))
