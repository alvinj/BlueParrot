package com.alvinalexander.blueparrot

import akka.actor.ActorSystem
import akka.actor.Props
import javax.swing.SwingUtilities
import javax.swing.UIManager
import com.apple.eawt.Application
import javax.swing.JFrame
import akka.actor.ActorRef
import akka.util.Timeout
import akka.dispatch.Await
import akka.dispatch.Await
import akka.dispatch.Future
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import java.util.prefs.Preferences
import java.io.File

class MainController extends MacOSXApplicationInterface {

  var preferences:Preferences = _
  var parrot:ActorRef = _
  val system = ActorSystem("BlueParrot")

  // phrases to speak
  val SLASH = System.getProperty("file.separator")
  val USER_HOME_DIR = System.getProperty("user.home")
  val BP_DATA_DIR = "Library/Application Support/com.alvinalexander/BlueParrot"
  val CANON_PHRASES_DIR = USER_HOME_DIR + SLASH + BP_DATA_DIR
  val CANON_PHRASES_FILENAME = USER_HOME_DIR + SLASH + BP_DATA_DIR + SLASH + "BlueParrot.phrases"
  val INITIAL_PHRASES = Array("Polly wants a cracker.", "Polly wants a drink.")
    
  // preferences
  var soundFileFolder:String = _
  var maxWaitTime:Int = _
  val PREF_SOUND_DIR_KEY = "SOUND_DIR"
  val PREF_MAX_WAIT_KEY = "MAX_WAIT_TIME"
    
  var phrasesToSpeak:Array[String] = _

  var macApplication:Application = _
  var macAdapter:MacOSXApplicationAdapter = _
  
  var mainFrameController:MainFrameController = _

  def start {
    connectToPreferences
    createPhrasesFileIfNeeded
    initActors
    initOurData
    configureMacOsXStuff
    displayTheUi
  }
  
  def connectToPreferences {
    preferences = Preferences.userNodeForPackage(this.getClass)
    soundFileFolder = preferences.get(PREF_SOUND_DIR_KEY, "")
    maxWaitTime = preferences.getInt(PREF_MAX_WAIT_KEY, 30)
  }

  def createPhrasesFileIfNeeded {
    val f = new File(CANON_PHRASES_FILENAME)
    if (!f.exists) {
      try {
        makeDirectories(CANON_PHRASES_DIR)
        FileUtils.writeStringsToFile(INITIAL_PHRASES, CANON_PHRASES_FILENAME)
      } catch {
        case e: Exception => // TODO do something here
      }
    }
  }

  def makeDirectories(directoryName: String): Boolean = {
    try {
      val result = (new File(directoryName)).mkdirs
      result
    }
    catch {
      case e: RuntimeException => false
    }
  }
  
  /**
   * Right now the actors know where the data is, so i have to get it from them.
   */
  def initOurData {
    implicit val timeout = Timeout(5 seconds)
    val future = parrot ? GetPhrasesToSpeak
    phrasesToSpeak = Await.result(future, timeout.duration).asInstanceOf[Array[String]]
  }
  
  def startTalking {
    parrot ! StartMessage
  }
  
  def stopTalking {
    parrot ! StopMessage
  }

  /**
   * Called from Swing when the user chooses a new folder.
   */
  def setSoundFileFolder(folder: String) { 
    soundFileFolder = folder
    preferences.put(PREF_SOUND_DIR_KEY, soundFileFolder)
    preferences.flush
    parrot ! SetSoundFolder(folder)
  }

  def setPhrasesToSpeak(a: Array[String]) { 
    phrasesToSpeak = a
    parrot ! SetPhrasesToSpeak(a)
  }

  def setMaxWaitTime(t: Int) { 
    maxWaitTime = t
    preferences.putInt(PREF_MAX_WAIT_KEY, maxWaitTime.toInt)
    preferences.flush
    parrot ! MaxWaitTime(t)
  }
  
  def getPhrasesAsMultilineString: String = {
    phrasesToSpeak.mkString("\n")
  }
  
  private def initActors {
    parrot = system.actorOf(Props(new RandomSpeakingActor(CANON_PHRASES_FILENAME, soundFileFolder, maxWaitTime)), name = "RandomSpeakingActor")
  }

  private def configureMacOsXStuff {
    macApplication = Application.getApplication
    configureOSXAboutPreferencesAndQuit(macApplication);
  }

  private def configureOSXAboutPreferencesAndQuit(theApplication: Application) {
    macAdapter = new MacOSXApplicationAdapter(this)
    theApplication.addApplicationListener(macAdapter)
    // must enable the preferences option manually, if wanted
    //theApplication.setEnabledPreferencesMenu(true);
  }

  private def displayTheUi {
    mainFrameController = new MainFrameController(this)
    mainFrameController.displayMainFrame
  }

  def doAboutAction {
  }

  def doPreferencesAction {
    // TODO if you implement this, you need to un-comment the "setEnabledPreferencesMenu" line in the main method
    //JOptionPane.showMessageDialog(null, "Sorry, no preferences at this time.");
  }

  def doQuitAction {
    SwingUtils.sleep(250)
    System.exit(0)
  }

}







