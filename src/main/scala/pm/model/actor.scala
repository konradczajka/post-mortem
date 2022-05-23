package pm.model

import scala.collection.immutable.Map

trait Actor:
  def initiative: Int
  def id: ActorId

type ActorId = String

val PLAYER_ID: ActorId = "actor-0"

case class Creature(id: ActorId, hp: Int, atk: Int, acc: Int, ai: Option[AI], initiative: Int) extends Actor
object Creature:
  def player(hp: Int, atk: Int, acc: Int, initiative: Int) = Creature(PLAYER_ID, hp, atk, acc, None, initiative)


case class Actors(actors: Map[ActorId, Actor]):
  def get(actorId: ActorId): Option[Actor] = actors.get(actorId)
  def update(actorId: ActorId, actor: Actor): Actors = copy(actors.updated(actorId, actor))
  def remove(actorId: ActorId): Actors = copy(actors - actorId)