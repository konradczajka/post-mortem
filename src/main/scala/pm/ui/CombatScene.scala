package pm.ui

import org.cosplay.{CPColor, CPDim, CPKeyboardSprite, CPLabelSprite, CPPixel, CPScene, CPSceneObjectContext}
import pm.model.{CombatStarted, Direction, PLAYER_ID}
import pm.model.Direction.*
import pm.system.MovementSystem.MoveAttempted
import pm.system.*
import org.cosplay.CPKeyboardKey.*
import pm.ui.system.CombatObjectsProjector

class CombatScene(dim: CPDim) extends CPScene("combat", Some(dim), CombatScene.BG_PX):

  override def onActivate(): Unit =
    Game.w = runIteration(Game.w, CombatStarted, Game.p)
    val keySpU = new CPKeyboardSprite(KEY_UP, _ => Game.w = runIteration(Game.w, MoveAttempted(PLAYER_ID, UP), Game.p))
    val keySpD = new CPKeyboardSprite(KEY_DOWN, _ => Game.w = runIteration(Game.w, MoveAttempted(PLAYER_ID, DOWN), Game.p))
    val keySpL = new CPKeyboardSprite(KEY_LEFT, _ => Game.w = runIteration(Game.w, MoveAttempted(PLAYER_ID, LEFT), Game.p))
    val keySpR = new CPKeyboardSprite(KEY_RIGHT, _ => Game.w = runIteration(Game.w, MoveAttempted(PLAYER_ID, RIGHT), Game.p))
    val keySpS = new CPKeyboardSprite(KEY_SPACE, _ => Game.w = runIteration(Game.w, Wait(PLAYER_ID), Game.p))
    val keySpAI = new CPKeyboardSprite(KEY_LO_I, _ => Game.w = runIteration(Game.w, MeleeAttackAttempted(PLAYER_ID, UP), Game.p))
    val keySpAK = new CPKeyboardSprite(KEY_LO_K, _ => Game.w = runIteration(Game.w, MeleeAttackAttempted(PLAYER_ID, DOWN), Game.p))
    val keySpAJ = new CPKeyboardSprite(KEY_LO_J, _ => Game.w = runIteration(Game.w, MeleeAttackAttempted(PLAYER_ID, LEFT), Game.p))
    val keySpAL = new CPKeyboardSprite(KEY_LO_L, _ => Game.w = runIteration(Game.w, MeleeAttackAttempted(PLAYER_ID, RIGHT), Game.p))
    val txt = new CPLabelSprite(x = 10, y =2, z = 0, fg=CPColor.C_AQUA, text = " Post Mortem test")
    addObjects(txt, keySpU, keySpD, keySpL, keySpR, keySpS, keySpAI, keySpAK, keySpAJ, keySpAL, CombatObjectsProjector.sprite)

object CombatScene:
  final val BG_PX: CPPixel = CPPixel('.', CPColor.C_GREY, CPColor.C_BLACK)
