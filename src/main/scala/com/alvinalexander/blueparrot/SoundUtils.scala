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

}