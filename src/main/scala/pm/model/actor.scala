package pm.model

import pm.system.Event

trait Actor

type ActorId = String

val PLAYER_ID: ActorId = "0"

case class Creature(id: ActorId, hp: Int, atk: Int) extends Actor
object Creature:
  def player(hp: Int, atk: Int) = Creature(PLAYER_ID, hp, atk)