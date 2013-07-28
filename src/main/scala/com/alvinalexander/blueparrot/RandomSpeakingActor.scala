package com.alvinalexander.blueparrot

import java.util.Properties
import scala.util.Random
import java.io.File
import com.alvinalexander.sound._
import com.alvinalexander.applescript._
import java.io.PrintWriter
import akka.actor._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

case object StartMessage
case object StopMessage
case class SetSoundLevelMessage(level: Int)  // 0 to 100
case class SetSoundFolder(folder: String)
case class MaxWaitTime(t: Int)
case class SetPhrasesToSpeak(phrases: Array[String])
case object GetPhrasesToSpeak
case object MakeRandomNoise
case object ScheduleNextSound

class RandomSpeakingActor(canonPhrasesFilename: String, 
                          rootSoundFileDir: String,
                          var maxWaitTime: Int) 
extends Actor
{
  val helper = context.actorOf(Props(new RandomSpeakingHelper(canonPhrasesFilename, rootSoundFileDir, maxWaitTime)), name = "RandomSpeakingHelper")

  def receive = {
    case StartMessage =>
         helper ! ScheduleNextSound
         
    case ScheduleNextSound =>
         helper ! ScheduleNextSound

    case StopMessage =>
         helper ! StopMessage

    case soundLevelMessage: SetSoundLevelMessage =>
         helper ! soundLevelMessage
         
    case SetSoundFolder(folder: String) =>
         helper ! SetSoundFolder(folder)
         
    case MaxWaitTime(time) =>
         helper ! MaxWaitTime(time)
         
    case GetPhrasesToSpeak =>
         implicit val timeout = Timeout(5 seconds)
         val future = helper ? GetPhrasesToSpeak
         val result = Await.result(future, timeout.duration).asInstanceOf[Array[String]]
         sender ! result
         
    case SetPhrasesToSpeak(phrases) =>
         helper ! SetPhrasesToSpeak(phrases)
      
    case _ => println("Got something unexpected.") 
  }
  
}


//---------------------- HELPER ------------------------

trait RandomThing
case class RandomFile(f: File) extends RandomThing
case class RandomString(s: String) extends RandomThing


/**
 * This helper class now does the heavy lifting.
 * TODO - Randomly get either (1) a file or (2) a text phrase to speak.
 * TODO - Create getRandomPhrase
 * TODO - Add ability to speak a phrase.
 *        AppleScriptUtils.speak("Hello, Al")
 *        AppleScriptUtils.speak("Hello, world", VICKI)
 * 
 */
class RandomSpeakingHelper(canonPhrasesFilename: String, 
                           var rootSoundFileDir: String,
                           var maxWaitTime: Int) 
extends Actor
{
  
  //val randomNoisePlugin:ActorRef = context.parent

  // properties
  private var properties:Properties = _
  private var phrasesToSpeak:Array[String] = _
  private var inSpeakingState = false
  private var soundLevel = 50  // 0 to 100
  
  val timer = context.actorOf(Props(new TimerHelper(maxWaitTime)), name = "TimerHelper")
  private var allSoundFiles:Array[File] = _

  def receive = {

    case ScheduleNextSound =>
         inSpeakingState = true
         timer ! ScheduleNextSound

    case StopMessage =>
         inSpeakingState = false

    case soundLevelMessage: SetSoundLevelMessage =>
         soundLevel = soundLevelMessage.level

    case SetSoundFolder(folder) =>
         rootSoundFileDir = folder

    case MaxWaitTime(time) =>
         maxWaitTime = time
         timer ! MaxWaitTime(time)

    case SetPhrasesToSpeak(phrases) =>
         savePhrasesToSpeak(phrases)

    case GetPhrasesToSpeak =>
         sender ! getPhrasesFromFilesystem
         
    case MakeRandomNoise =>
         makeRandomNoise
         timer ! ScheduleNextSound

    case _ => println("got unexpected message")

  }

  def makeRandomNoise {
    if (!inSpeakingState) return
    getRandomThing match {
      case RandomString(s) => speakText(s)
      case RandomFile(f) => playSoundFile(f)
    }
  }

  /**
   * Gets all strings and files, then returns either a RandomString or
   * RandomFile.
   * 
   * Note that if there are 9 files and 1 string, there is a 90% chance
   * of getting a file. (This differs from the old approach.)
   */
  def getRandomThing: RandomThing = {
    val phrases = getPhrasesFromFilesystem
    val soundFiles = getRecursiveListOfSoundFiles(rootSoundFileDir)
    val all = new ArrayBuffer[RandomThing]
    for(s <- phrases) all += RandomString(s)
    for(f <- soundFiles) all += RandomFile(f)
    val r = new Random(System.currentTimeMillis)
    val i = r.nextInt(all.size)
    all(i)
  }

  // TODO add the ability to speak using different voices
  // TODO use the new volume setting
  def speakText(textToSay: String) {
    if (textToSay.contains("|")) {
      val parts = textToSay.split('|')
      val text = parts(0).trim
      val voice = parts(1).trim.toUpperCase
      AppleScriptUtils.speak(text, voice)
      
    } else {
      AppleScriptUtils.speak(textToSay)
    }
    //AppleScriptUtils.speak("Hello, world", VICKI)
  }
  
  def savePhrasesToSpeak(phrases: Array[String]) {
    // write these new phrases to the proper file; the rest of the code will pick it up from there
    //canonPhrasesFilename
    val out = new PrintWriter(canonPhrasesFilename)
    try {
      for (s <- phrases) out.println(s)
    }
    finally{ out.close }
  }

  def getPhrasesFromFilesystem: Array[String] = {
    FileUtils.getFileContentsAsListBlanksCommentsRemoved(canonPhrasesFilename).toArray
  }
  
  def playSoundFile(f: File) {
    SoundUtils.playSoundFile(f.getCanonicalPath, soundLevel)
  }
  
  /**
   * Get a recursive list of all sound files, presumably from beneath the plugin dir.
   */
  def getRecursiveListOfSoundFiles(dirName: String):Array[File] = {
    val files = FileUtils.getRecursiveListOfFiles(new File(dirName))
    for {
      file <- files 
      if hasSoundFileExtension(file)
      if !soundFileIsLong(file)
    } yield file
  }

  def soundFileIsLong(f: File) = {
    if (f.getName.toLowerCase.contains("long")) true
    else false
  }
  
  def hasSoundFileExtension(file: File):Boolean = {
    val okFileExtensions = Array("wav", "mp3", "aiff")
    for (extension <- okFileExtensions) {
      if (file.getName.toLowerCase.endsWith(extension)) return true
    }
    false
  }

}

//------------------------ TIMER -------------------------

/**
 * This actor should only be started/called by the helper.
 */
class TimerHelper(var maxWaitTime: Int) extends Actor {
  def receive = {
    case ScheduleNextSound =>
      var randomWaitTime = getRandomWaitTime(maxWaitTime)
      //println("maxWaitTime(%s), randomWaitTime(%s)".format(maxWaitTime, randomWaitTime))
      context.system.scheduler.scheduleOnce(Duration.create(randomWaitTime, TimeUnit.SECONDS), context.parent, MakeRandomNoise)
    case MaxWaitTime(time) =>
      maxWaitTime = time
  }
}













