package com.alvinalexander.blueparrot

import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.BorderLayout
import javax.swing.event.HyperlinkListener
import javax.swing.event.HyperlinkEvent
import javax.swing.JLabel
import javax.swing.JButton
import java.awt.FlowLayout

class MainFrame extends JFrame {
  
  val mainImagePanel = new MainImagePanel
  val actionPanel = new ActionPanel
  
  getContentPane.add(mainImagePanel,BorderLayout.CENTER)
  getContentPane.add(actionPanel,BorderLayout.SOUTH)
  pack
  
}


class MainImagePanel extends JPanel {
  
}

class ActionPanel extends JPanel {
  
  val editSoundFileFolderWidget = new JButton("Sound File Folder")
  val editPhrasesWidget = new JButton("Text Phrases")
  val editMaxWaitTimeWidget = new JButton("Max Wait Time")
  val startStopButton = new JButton("Start")
  
  setLayout(new FlowLayout)
  add(startStopButton)
  add(editSoundFileFolderWidget)
  add(editPhrasesWidget)
  add(editMaxWaitTimeWidget)
  
}















