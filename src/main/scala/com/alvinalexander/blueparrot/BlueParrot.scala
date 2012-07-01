package com.alvinalexander.blueparrot

import com.alvinalexander.applescript._

/**
 * Have to manually import the related GitHub projects, per this url:
 * https://github.com/typesafehub/sbteclipse/issues/48
 * see BuildPath > Projects
 * 
 */
object BlueParrot {
  
  def main(args: Array[String]) {
    println("Hello, world")
    AppleScriptUtils.speak("Hello, Al")
    AppleScriptUtils.speak("Hello, world", VICKI)
  }

}
