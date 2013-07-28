package com.alvinalexander.blueparrot

import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.event.HyperlinkListener
import javax.swing.event.HyperlinkEvent
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.event.ChangeListener
import javax.swing.event.ChangeEvent
import javax.swing.JSlider

/**
 * This is the controller for the MainFrame.
 * It responds to the user interacting with the MainFrame.
 */
class MainFrameController(mainController: MainController) extends ChangeListener {
  
  // init variables
  val mainFrame = new MainFrame
  var lastDirectory = ""
  var volumeGain = 50

  // get a reference to all the mainframe widgets
  val editSoundFileFolderWidget = mainFrame.actionPanel.editSoundFileFolderWidget
  val editPhrasesWidget = mainFrame.actionPanel.editPhrasesWidget
  val editMaxWaitTimeWidget = mainFrame.actionPanel.editMaxWaitTimeWidget
  val startStopButton = mainFrame.actionPanel.startStopButton
  val volumeSlider = mainFrame.actionPanel.volumeSlider

  // handle the process of the user selecting a new sound file folder
  val editSoundFileFolderListener = new ActionListener {
    def actionPerformed(e: ActionEvent) {
      System.setProperty("apple.awt.fileDialogForDirectories", "true")
      val d = SwingUtils.letUserChooseFile(mainFrame, lastDirectory, "Select a Directory")
      val dir = d.getDirectory
      if (dir == null || dir.trim == "" || dir == lastDirectory) {
        // do nothing
      } else {
        // actually get the last selected dir as a file
        val canonDir = dir + d.getFile
        lastDirectory = canonDir
        mainController.setSoundFileFolder(canonDir)
      }
      System.setProperty("apple.awt.fileDialogForDirectories", "false")
    }
  }

  // handle the process of the user adding new phrases the parrot should speak
  val editPhrasesListener = new ActionListener {
    def actionPerformed(e: ActionEvent) {
      val d = new EditPhrasesDialog(mainFrame)
      val ta = d.getTextArea
      ta.setText(mainController.getPhrasesAsMultilineString)
      
      val okButton = d.getOkButton
      val cancelButton = d.getCancelButton
      
      okButton.addActionListener(new ActionListener {
        def actionPerformed(e: ActionEvent) {
          d.setVisible(false)
          // TODO add some error checking here
          val s = ta.getText
          mainController.setPhrasesToSpeak(s.split("\n"))
        }
      })
      
      cancelButton.addActionListener(new ActionListener {
        def actionPerformed(e: ActionEvent) {
          // do nothing
          d.setVisible(false)
        }
      })
      
      d.setModal(true)
      d.setVisible(true)
    }
  }
  
  // handle the process of the user changing the max wait time
  val editMaxWaitTimeListener = new ActionListener {
    def actionPerformed(e: ActionEvent) {
      // using this option so i can set the dialog title
      val result = JOptionPane.showInputDialog(mainFrame, 
          "Max. Wait Time (in seconds)",
          "Maximum Wait Time",
          JOptionPane.QUESTION_MESSAGE,
          null,
          null,
          mainController.maxWaitTime)
      result match {
        case null => return
        case s:String if s.trim == "" => return
        case s:String => updateMaxWaitTimeIfStringIsValidInt(s)
        case _ => return  // proverbial "can't happen"
      }
    }
  }

  private def updateMaxWaitTimeIfStringIsValidInt(secondsAsString: String) {
    try {
      val seconds = secondsAsString.toInt
      mainController.setMaxWaitTime(seconds)
    } catch {
      case e: Exception => // do nothing
    }
  }
  
  // handle the process of the user pressing the start/stop button
  val startStopButtonListener = new ActionListener {
    def actionPerformed(e: ActionEvent) {
      val button = e.getSource.asInstanceOf[JButton]
      if (button.getText == "Start") {
        mainFrame.mainImagePanel.setBackground(MainFrame.colorWhenRunning)
        mainController.startTalking
        button.setText("Stop")
      } else {
        button.setText("Start")
        mainFrame.mainImagePanel.setBackground(MainFrame.colorWhenNotRunning)
        mainController.stopTalking
      }
    }
  }
  
  def addListenersToWidgets {
    editSoundFileFolderWidget.addActionListener(editSoundFileFolderListener)
    editPhrasesWidget.addActionListener(editPhrasesListener)
    editMaxWaitTimeWidget.addActionListener(editMaxWaitTimeListener)
    startStopButton.addActionListener(startStopButtonListener)
    volumeSlider.addChangeListener(this)
  }

  def configureVolumeSlider {
    volumeSlider.setValue(volumeGain)
  }

  /**
   * This method gets a callback whenever the JSlider control is adjusted.
   * Values are 0 to 100 (Int).
   */
  @Override
  def stateChanged(event: ChangeEvent) {
    val source = event.getSource.asInstanceOf[JSlider]
    if (!source.getValueIsAdjusting()) {
      var volume = source.getValue.asInstanceOf[Int]
      mainController.setGain(volume)
    }
  }
  
  def displayMainFrame {
    SwingUtilities.invokeLater(new Runnable()
    {
      def run
      {
        try
        {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
          mainFrame.setLocationRelativeTo(null)
          mainFrame.setResizable(false)
          mainFrame.setVisible(true)
        }
        catch {
          case e:Exception => e.printStackTrace
        }
      }
    })
  }

  //------------------------------ the action ----------------------------
  
  configureVolumeSlider
  addListenersToWidgets

}







