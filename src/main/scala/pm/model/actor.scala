package pm.model

import pm.system.Event

import scala.collection.immutable.Map

trait Actor

type ActorId = String

val PLAYER_ID: ActorId = "0"

case class Creature(id: ActorId, hp: Int, atk: Int, ai: Option[AI]) extends Actor
object Creature:
  def player(hp: Int, atk: Int) = Creature(PLAYER_ID, hp, atk, None)


case class Actors(actors: Map[ActorId, Actor]):
  def get(actorId: ActorId): Option[Actor] = actors.get(actorId)
  def update(actorId: ActorId, actor: Actor): Actors = copy(actors.updated(actorId, actor))
  def remove(actorId: ActorId): Actors = copy(actors - actorId)