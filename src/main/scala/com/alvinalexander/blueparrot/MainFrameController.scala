package com.alvinalexander.blueparrot

import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.event.HyperlinkListener
import javax.swing.event.HyperlinkEvent
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

class MainFrameController(mainController: MainController) {
  
  val mainFrame = new MainFrame
  val editSoundFileFolderWidget = mainFrame.actionPanel.editSoundFileFolderWidget
  val editPhrasesWidget = mainFrame.actionPanel.editPhrasesWidget
  val editMaxWaitTimeWidget = mainFrame.actionPanel.editMaxWaitTimeWidget
  
  editSoundFileFolderWidget.addActionListener(editSoundFileFolderListener)
  editPhrasesWidget.addActionListener(editPhrasesListener)
  editMaxWaitTimeWidget.addActionListener(editMaxWaitTimeListener)

  val editSoundFileFolderListener = new ActionListener {
    def actionPerformed(e: ActionEvent) {
      // TODO
      // display file dialog
      // get the directory
    }
  }

  val editPhrasesListener = new ActionListener {
    def actionPerformed(e: ActionEvent) {
      // TODO
      // display an EditPhrasesDialog; i can use my "Big Dialog" recipe
      // update the main controller if the user didn't cancel
    }
  }

  val editMaxWaitTimeListener = new ActionListener {
    def actionPerformed(e: ActionEvent) {
      // TODO
      // display a dialog; use built-in dialogs
      // update the main controller if the user didn't cancel
    }
  }
  
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