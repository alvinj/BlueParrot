package com.alvinalexander.blueparrot

import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.event.HyperlinkListener
import javax.swing.event.HyperlinkEvent
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JOptionPane

class MainFrameController(mainController: MainController) {
  
  val mainFrame = new MainFrame
  mainFrame.setTitle("The Blue Parrot")
  var lastDirectory = ""
  val editSoundFileFolderWidget = mainFrame.actionPanel.editSoundFileFolderWidget
  val editPhrasesWidget = mainFrame.actionPanel.editPhrasesWidget
  val editMaxWaitTimeWidget = mainFrame.actionPanel.editMaxWaitTimeWidget
  val startStopButton = mainFrame.actionPanel.startStopButton
  
  val editSoundFileFolderListener = new ActionListener {
    def actionPerformed(e: ActionEvent) {
      System.setProperty("apple.awt.fileDialogForDirectories", "true");
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
      System.setProperty("apple.awt.fileDialogForDirectories", "false");
    }
  }

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

  editSoundFileFolderWidget.addActionListener(editSoundFileFolderListener)
  editPhrasesWidget.addActionListener(editPhrasesListener)
  editMaxWaitTimeWidget.addActionListener(editMaxWaitTimeListener)
  startStopButton.addActionListener(startStopButtonListener)

  def displayMainFrame {
    SwingUtilities.invokeLater(new Runnable()
    {
      def run
      {
        try
        {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          mainFrame.setLocationRelativeTo(null);
          mainFrame.setResizable(false);
          mainFrame.setVisible(true);
        }
        catch {
          case e:Exception => e.printStackTrace
        }
      }
    });
  }

}







