package pm.system

import cats.*
import cats.data.State
import cats.data.State.*
import cats.implicits.*
import monocle.*
import monocle.macros.GenLens
import monocle.syntax.all.*
import pm.model.World

import scala.annotation.tailrec

// TODO: Monoid?
case class CurrentEvents(current: Option[Event], collected: List[Event])
object CurrentEvents:
  def empty: CurrentEvents = CurrentEvents(None, List())

trait System[A]:
  def apply(stateL: Lens[World, A]): State[World, Unit] = State(
    m => {
      World.currentEventL.get(m) match
        case Some(e) =>
          val (nws, oe) = run(stateL.get(m), e)
          ((stateL.replace(nws) compose World.collectedEventsL.modify(_ ::: oe)) (m), ())
        case None => (m, ())
    })

  def run(ws: A, e: Event): (A, List[Event])

def programFromSystems(systems: List[State[World, Unit]]): State[World, Unit] =
  systems.tail.foldLeft(systems.head)((s1, s2) => s1.flatMap(_ => s2))

object EventLoggingSystem extends System[Unit]:
  def run(ws: Unit, e: Event): (Unit, List[Event]) =
    println(e)
    ((), Nil)

//
//@main
//def systemTest: Unit =
//  val initialPosition = Coordinate(2, 2)
//  val initialWorld = World(initialPosition, 0, None, List[Event]())
//
//  val ecL = GenLens[World](_.ec)
//  val cL = GenLens[World](_.c)
//  val events = List(MoveAttempted(Direction.UP), MoveAttempted(Direction.RIGHT), MoveAttempted(Direction.UP))
//  val systems = List(TestMoveSystem(cL), TestEventCounterSystem(ecL))
//
//  val p = systems.tail.foldLeft(systems.head)((s1, s2) => s1.flatMap(_ => s2))
//
//  val r = events.foldLeft(initialWorld)((w, e) => runIteration(w, e, p))
//
//  println(r)

@tailrec
def runIteration(w: World, e: Event, p: State[World, Unit]): World =
  val nw = p.runS(World.currentEventL.replace(Some(e))(w)).value
  World.collectedEventsL.get(nw) match
    case e :: rest =>
      runIteration(
        (World.currentEventL.replace(Some(e)) compose World.collectedEventsL.replace(rest)) (nw),
        e, p)
    case Nil => World.currentEventL.replace(None)(nw)

def runEvents(program: State[World, Unit], initialWorld: World, events: List[Event]): World =
  events.foldLeft(initialWorld)((w, e) => runIteration(w, e, program))