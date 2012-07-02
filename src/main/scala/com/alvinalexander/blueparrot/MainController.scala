package com.alvinalexander.blueparrot

import akka.actor.ActorSystem
import akka.actor.Props
import javax.swing.SwingUtilities
import javax.swing.UIManager
import com.apple.eawt.Application;
import javax.swing.JFrame

class MainController extends MacOSXApplicationInterface {

  val canonPropsFilename = "/Users/al/Projects/Scala/BlueParrot/testing/blueparrot.props"
  val canonPhrasesFilename = "/Users/al/Projects/Scala/BlueParrot/testing/blueparrot.phrases"
    
  var soundFileFolder:String = _
  var phrasesToSpeak:Array[String] = _
  var maxWaitTime:Long = _

  var macApplication:Application = _
  var macAdapter:MacOSXApplicationAdapter = _
  
  var mainFrameController:MainFrameController = _

  def start {
    startActors
    configureMacOsXStuff
    displayTheUi
  }

  // the user can update these
  def setSoundFileFolder(s: String) { soundFileFolder = s }
  def setPhrasesToSpeak(a: Array[String]) { phrasesToSpeak = a }
  def setMaxWaitTime(t: Long) { maxWaitTime = t }
  
  def startActors {
    val system = ActorSystem("BlueParrot")
    val parrot = system.actorOf(Props(new RandomSpeakingActor(canonPropsFilename, canonPhrasesFilename )), name = "RandomSpeakingActor")
    parrot ! StartMessage
  }

  def configureMacOsXStuff {
    macApplication = Application.getApplication
    configureOSXAboutPreferencesAndQuit(macApplication);
  }

  def configureOSXAboutPreferencesAndQuit(theApplication: Application) {
    macAdapter = new MacOSXApplicationAdapter(this)
    theApplication.addApplicationListener(macAdapter)
    // must enable the preferences option manually, if wanted
    //theApplication.setEnabledPreferencesMenu(true);
  }

  def displayTheUi {
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