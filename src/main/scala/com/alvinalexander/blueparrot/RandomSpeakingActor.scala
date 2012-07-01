package com.alvinalexander.blueparrot

import java.util.Properties
import scala.util.Random
import java.io.File
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.Actor
import com.alvinalexander.sound._
import com.alvinalexander.applescript._

case class StartMessage
case class StopMessage

class RandomSpeakingActor(canonPropsFilename: String, canonPhrasesFilename: String) 
extends Actor
{
  val helper:ActorRef = context.actorOf(Props(new RandomSpeakingHelper(canonPropsFilename, canonPhrasesFilename)), name = "RandomSpeakingHelper")

  def receive = {
    case StartMessage =>
         println("starting main actor")
         startMainLoop
    case StopMessage =>
         println("got shutdown message, shutting down")
         context.stop(self)
         context.system.shutdown
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
  private val PROPERTIES_REL_FILENAME = "RandomNoise.properties"
  private val PROP_MAX_TIME_KEY = "max_wait_time"  // key in the properties file
  private val PROP_ROOT_SOUNDFILE_DIR = "soundfile_dir"

  // read properties file before anything else
  readConfigFile

  private var allSoundFiles:Array[File] = _

  // may want to do this dynamically, on every loop
  allSoundFiles = getRecursiveListOfSoundFiles(rootSoundFileDir)

  def receive = {

    case StartMessage =>
         println("starting actor helper")
         startLoop

    case _ => println("got unexpected message")

  }

  // TODO set this back to a minute when done testing
  def sleepForAMinute = Thread.sleep(1*1000)

  def startLoop {
    var count = 0
    var randomWaitTime = getRandomWaitTimeInMinutes(maxWaitTime)
    while (true) {
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

  /**
   * Get a random phrase from the list of phrases we know.
   */
  def getRandomPhrase:String = {
    // get all known phrases from our file/database
    println("finding phrases to speak ...")
    val strings = FileUtils.getFileContentsAsList(canonPhrasesFilename)
    println("found this many phrases: " + strings.size)
    val r = getRandomIntFromZeroUpToMaxExclusive(strings.size)
    strings(r)
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
  






