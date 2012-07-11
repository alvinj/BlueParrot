package com.alvinalexander.blueparrot

import java.util.Properties
import scala.util.Random
import java.io.File
import com.alvinalexander.sound._
import com.alvinalexander.applescript._
import java.io.PrintWriter
import akka.actor._
import akka.dispatch.Await
import akka.dispatch.Future
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import akka.util.Duration
import java.util.concurrent.TimeUnit
import scala.collection.mutable.ArrayBuffer

case object StartMessage
case object StopMessage
case class SetSoundFolder(folder: String)
case class MaxWaitTime(t: Long)
case class SetPhrasesToSpeak(phrases: Array[String])
case object GetPhrasesToSpeak
case object MakeRandomNoise
case object ScheduleNextSound

class RandomSpeakingActor(canonPropsFilename: String, canonPhrasesFilename: String) 
extends Actor
{
  val helper = context.actorOf(Props(new RandomSpeakingHelper(canonPropsFilename, canonPhrasesFilename)), name = "RandomSpeakingHelper")

  def receive = {
    case StartMessage =>
         println("main actor rec'd StartMessage")
         //startMainLoop
         helper ! ScheduleNextSound
         
    case ScheduleNextSound =>
         helper ! ScheduleNextSound

    case StopMessage =>
         println("main actor rec'd StopMessage")
         helper ! PoisonPill
         //context.stop(helper)
         //helper ! StopMessage
         // TODO stop the worker thread
         //context.stop(self)
         //context.system.shutdown

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
class RandomSpeakingHelper(canonPropsFilename: String, canonPhrasesFilename: String) 
extends Actor
{
  
  //val randomNoisePlugin:ActorRef = context.parent

  // properties
  private var properties:Properties = _
  private var maxWaitTime = 20
  private var rootSoundFileDir:String = _
  private var phrasesToSpeak:Array[String] = _
  private val PROPERTIES_REL_FILENAME = "RandomNoise.properties"
  private val PROP_MAX_TIME_KEY = "max_wait_time"  // key in the properties file
  private val PROP_ROOT_SOUNDFILE_DIR = "soundfile_dir"
  private var inSpeakingState = false
  
  val timer = context.actorOf(Props(new TimerHelper(maxWaitTime)), name = "TimerHelper")
  getPropertiesFromConfigFile
  private var allSoundFiles:Array[File] = _

  def receive = {

    case ScheduleNextSound =>
         inSpeakingState = true
         timer ! ScheduleNextSound

    case StopMessage =>
         inSpeakingState = false
         
    case SetSoundFolder(folder) =>
         rootSoundFileDir = folder

    case MaxWaitTime(time) =>
         maxWaitTime = time.toInt
         timer ! MaxWaitTime(time)

    case SetPhrasesToSpeak(phrases) =>
         updatePhrasesToSpeak(phrases)

    case GetPhrasesToSpeak =>
         sender ! getPhrasesFromFilesystem
         
    case MakeRandomNoise =>
         makeRandomNoise
         timer ! ScheduleNextSound

    case _ => println("got unexpected message")

  }

  // TODO set this back to a minute when done testing
  //def sleepForAMinute = Thread.sleep(1*1000)
  
  def makeRandomNoise {
    if (!inSpeakingState) return
    getRandomThing match {
      case RandomString(s) =>
           speakText(s)
      case RandomFile(f) =>
           playSoundFile(f)
    }
  }

  //def getRandom0Or1: Int = new Random(System.currentTimeMillis).nextInt(2)

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
  def speakText(textToSay: String) {
    AppleScriptUtils.speak(textToSay)
    //AppleScriptUtils.speak("Hello, world", VICKI)
  }
  
  def updatePhrasesToSpeak(phrases: Array[String]) {
    // write these new phrases to the proper file; the rest of the code will pick it up from there
    println("writing to file")
    canonPhrasesFilename
    val out = new PrintWriter(canonPhrasesFilename)
    try {
      for (s <- phrases) out.println(s)
    }
    finally{ out.close }
  }

  /**
   * Get a random phrase from the list of phrases we know.
   */
  def getRandomPhrase:String = {
    // get all known phrases from our file/database
    val strings = getPhrasesFromFilesystem
    val r = getRandomIntFromZeroUpToMaxExclusive(strings.size)
    strings(r)
  }
  
  def getPhrasesFromFilesystem: Array[String] = {
    FileUtils.getFileContentsAsList(canonPhrasesFilename).toArray
  }
  
  def getPropertiesFromConfigFile {
    try {
      properties = FileUtils.readPropertiesFile(canonPropsFilename)
      maxWaitTime = properties.getProperty(PROP_MAX_TIME_KEY).toInt
      rootSoundFileDir = properties.getProperty(PROP_ROOT_SOUNDFILE_DIR)
    } catch {
      case e:Exception => e.printStackTrace
    }
  }
  
  def playSoundFile(f: File) {
    try {
      val player = SoundFilePlayer.getSoundFilePlayer(f.getCanonicalPath)
      player.play
    } catch {
      case e:Exception => e.printStackTrace
    }
  }
  
  def getRandomSoundFile:File = {
    // do this all the time, so i can adjust to new sound file dirs
    allSoundFiles = getRecursiveListOfSoundFiles(rootSoundFileDir)
    val r = new Random(System.currentTimeMillis)
    val i = r.nextInt(allSoundFiles.size)
    println("next sound file index: " + i)
    println("# of sound files: " + allSoundFiles.size)
    println("# of sound files: " + allSoundFiles.length)
    return allSoundFiles(i)
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
      var randomWaitTime = getRandomWaitTimeInMinutes(maxWaitTime)
      println("maxWaitTime(%s), randomWaitTime(%s)".format(maxWaitTime, randomWaitTime))
      context.system.scheduler.scheduleOnce(Duration.create(randomWaitTime, TimeUnit.SECONDS), context.parent, MakeRandomNoise)
    case MaxWaitTime(time) =>
      maxWaitTime = time.toInt
  }
}













