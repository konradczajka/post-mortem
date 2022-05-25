package pm.system

import cats.*
import cats.data.State
import cats.data.State.*
import cats.implicits.*
import monocle.*
import monocle.macros.GenLens
import monocle.syntax.all.*
import pm.model.{Event, World, WorldLens}

import scala.annotation.tailrec

// TODO: Monoid?
case class CurrentEvents(current: Option[Event], collected: List[Event])

object CurrentEvents:
  def empty: CurrentEvents = CurrentEvents(None, List())

type SystemAction = World => World

trait System[A]:
  def apply()(using wl: WorldLens[A]): SystemAction =
    w =>
      World.currentEventL.get(w) match
        case Some(e) =>
          val (nws, oe) = run(wl.lens.get(w), e)
          (wl.lens.replace(nws) compose World.collectedEventsL.modify(_ ::: oe)) (w)
        case None => w


  def run(ws: A, e: Event): (A, List[Event])

def programFromSystems(systems: List[SystemAction]): SystemAction =
  systems.foldLeft((w => w): SystemAction)((s1, s2) => s1 andThen s2)

object EventLoggingSystem extends System[Unit] :
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
def runIteration(w: World, e: Event, p: SystemAction): World =
  val nw = p.apply(World.currentEventL.replace(Some(e))(w))
  World.collectedEventsL.get(nw) match
    case e :: rest =>
      runIteration(
        (World.currentEventL.replace(Some(e)) compose World.collectedEventsL.replace(rest)) (nw),
        e, p)
    case Nil => World.currentEventL.replace(None)(nw)

def runEvents(program: SystemAction, initialWorld: World, events: List[Event]): World =
  events.foldLeft(initialWorld)((w, e) => runIteration(w, e, program))