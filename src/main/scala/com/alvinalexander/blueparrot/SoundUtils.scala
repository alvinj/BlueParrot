package com.alvinalexander.blueparrot

import sys.process._
import scala.language.postfixOps

object SoundUtils {
  
  /**
   * Play the given sound file with the Mac 'afplay' command,
   * at the sound file's natural volume.
   */
  def playSoundFile(canonFilename: String): Int = {
    val cmd = "afplay " + canonFilename
    val exitCode = (cmd !)  // val r = cmd ! was not an Int?
    exitCode
  }

  /**
   * Play the given sound file with the Mac 'afplay' command,
   * at the specified volume level. Specify a value from 0 to 100.
   */
  def playSoundFile(canonFilename: String, volumeLevel: Int): Int = {
    val volume = convertVolumeForAfplay(volumeLevel)
    val cmd = s"afplay -v $volume $canonFilename"
    val exitCode = (cmd !)  // val r = cmd ! was not an Int?
    exitCode
  }

  /**
   * Converts the volume to a Double, for use with the Mac 'afplay' command.
   */
  def convertVolumeForAfplay(volume: Int): Double = volume / 500.0 

}