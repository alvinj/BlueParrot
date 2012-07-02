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

case object StartMessage
case object StopMessage
case class SetSoundFolder(folder: String)
case class MaxWaitTime(t: Long)
case class SetPhrasesToSpeak(phrases: Array[String])
case object GetPhrasesToSpeak

class RandomSpeakingActor(canonPropsFilename: String, canonPhrasesFilename: String) 
extends Actor
{
  val helper:ActorRef = context.actorOf(Props(new RandomSpeakingHelper(canonPropsFilename, canonPhrasesFilename)), name = "RandomSpeakingHelper")

  def receive = {
    case StartMessage =>
         println("main actor rec'd StartMessage")
         startMainLoop

    case StopMessage =>
         println("main actor rec'd StopMessage")
         helper ! StopMessage
         // context.stop(helper)
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
  
  def startMainLoop {
    helper ! StartMessage
  }
  
}



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
    
  private var inRunningState = false

  // read properties file before anything else
  readConfigFile

  private var allSoundFiles:Array[File] = _

  def receive = {

    case StartMessage =>
         println("helper rec'd StartMessage")
         inRunningState = true
         startLoop

    case StopMessage =>
         println("helper rec'd StopMessage")
         inRunningState = false

    case SetSoundFolder(folder) =>
         rootSoundFileDir = folder

    case MaxWaitTime(time) =>
         maxWaitTime = time.toInt

    case SetPhrasesToSpeak(phrases) =>
         updatePhrasesToSpeak(phrases)

    case GetPhrasesToSpeak =>
         sender ! getPhrasesFromFilesystem

    case _ => println("got unexpected message")

  }

  // TODO set this back to a minute when done testing
  def sleepForAMinute = Thread.sleep(1*1000)

  def startLoop {
    var count = 0
    var randomWaitTime = getRandomWaitTimeInMinutes(maxWaitTime)
    while (true && inRunningState) {
      println("maxWaitTime: " + maxWaitTime)
      println("randomWaitTime: " + randomWaitTime)
      sleepForAMinute
      count += 1
      println("count: " + count)
      if (count >= randomWaitTime) {
        getRandom0Or1 match {
          case 0 => playSoundFile(getRandomSoundFile)
          case 1 => speakText(getRandomPhrase)
        }
        count = 0
        randomWaitTime = getRandomWaitTimeInMinutes(maxWaitTime)
      }
    }
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
  
  def readConfigFile {
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
  
  def getRandom0Or1: Int = {
    val r = new Random(System.currentTimeMillis)
    r.nextInt(2)
  }

  /**
   * Get a recursive list of all sound files, presumably from beneath the plugin dir.
   */
  def getRecursiveListOfSoundFiles(dirName: String) = {
    val files = FileUtils.getRecursiveListOfFiles(new File(dirName))
    for (file <- files 
        if hasSoundFileExtension(file)
        if !soundFileIsLong(file)) yield file
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
  






