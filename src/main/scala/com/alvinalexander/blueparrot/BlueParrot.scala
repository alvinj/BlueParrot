package com.alvinalexander.blueparrot

import com.alvinalexander.applescript._
import akka.actor.ActorSystem
import akka.actor.Props

/**
 * Have to manually import the related GitHub projects, per this url:
 * https://github.com/typesafehub/sbteclipse/issues/48
 * see BuildPath > Projects
 * 
 */
object BlueParrot {
    
  val canonPropsFilename = "/Users/al/Projects/Scala/BlueParrot/testing/blueparrot.props"
  val canonPhrasesFilename = "/Users/al/Projects/Scala/BlueParrot/testing/blueparrot.phrases"
    
  def main(args: Array[String]) {
    println("Blue Parrot is starting ...")
    val system = ActorSystem("BlueParrot")
    val parrot = system.actorOf(Props(new RandomSpeakingActor(canonPropsFilename, canonPhrasesFilename )), name = "RandomSpeakingActor")
    parrot ! StartMessage

    // run for a bit, then stop
    Thread.sleep(90*1000)
    parrot ! StopMessage
  }
  
}


