package com.alvinalexander.blueparrot

import sys.process._

object SoundUtils {
  
  /**
   * Play the given sound file with the Mac 'afplay' command.
   */
  def playSoundFile(canonFilename: String): Int = {
    val cmd = "afplay " + canonFilename
    val exitCode = (cmd !)  // val r = cmd ! was not an Int?
    exitCode
  }

  /**
   * Play the given sound file with the Mac 'afplay' command,
   * at the specified volume level. The level is 0 to 100.
   */
  def playSoundFile(canonFilename: String, volumeLevel: Double): Int = {
    val cmd = s"afplay -v $volumeLevel $canonFilename"
    val exitCode = (cmd !)  // val r = cmd ! was not an Int?
    exitCode
  }

}