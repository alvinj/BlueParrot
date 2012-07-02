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

class MainController extends MacOSXApplicationInterface {
  
  var parrot:ActorRef = _
  val system = ActorSystem("BlueParrot")

  val canonPropsFilename = "/Users/al/Projects/Scala/BlueParrot/testing/blueparrot.props"
  val canonPhrasesFilename = "/Users/al/Projects/Scala/BlueParrot/testing/blueparrot.phrases"
    
  var soundFileFolder:String = _
  var phrasesToSpeak:Array[String] = _
  var maxWaitTime:Long = _

  var macApplication:Application = _
  var macAdapter:MacOSXApplicationAdapter = _
  
  var mainFrameController:MainFrameController = _

  def start {
    initActors
    initOurData
    configureMacOsXStuff
    displayTheUi
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
    println("startTalking")
    parrot ! StartMessage
  }
  
  def stopTalking {
    println("stopTalking")
    parrot ! StopMessage
  }

  // the user can update these
  def setSoundFileFolder(s: String) { 
    // TODO need to save this as a property
    soundFileFolder = s
    parrot ! SetSoundFolder(s)
  }

  def setPhrasesToSpeak(a: Array[String]) { 
    phrasesToSpeak = a
    parrot ! SetPhrasesToSpeak(a)
  }

  def setMaxWaitTime(t: Long) { 
    maxWaitTime = t
    parrot ! MaxWaitTime(t)
  }
  
  def getPhrasesAsMultilineString: String = {
    phrasesToSpeak.mkString("\n")
  }

  
  private def initActors {
    parrot = system.actorOf(Props(new RandomSpeakingActor(canonPropsFilename, canonPhrasesFilename )), name = "RandomSpeakingActor")
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







