package com.alvinalexander.blueparrot

import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.BorderLayout
import javax.swing.event.HyperlinkListener
import javax.swing.event.HyperlinkEvent
import javax.swing.JLabel
import javax.swing.JButton
import java.awt.FlowLayout
import javax.swing.ImageIcon
import java.awt.Color
import javax.swing.JSlider
import javax.swing.SwingConstants
import java.util.Hashtable
import java.awt.GridLayout

class MainFrame extends JFrame {

  val mainImagePanel = new MainImagePanel
  val actionPanel = new ActionPanel
  
  getContentPane.add(mainImagePanel,BorderLayout.CENTER)
  getContentPane.add(actionPanel,BorderLayout.SOUTH)
  pack

}

object MainFrame {
  val pastelYellow = new Color(255, 255, 102)
  val boldYellow = new Color(255, 255, 34)
  val colorWhenRunning = boldYellow
  val colorWhenNotRunning = Color.GRAY
}

class MainImagePanel extends JPanel {
  
  // jar approach
  var parrotImage: ImageIcon = _
  
  try {
    // try getting it out of the jar (production)
    parrotImage = new ImageIcon(getClass.getResource("blueparrot.png"))
  } catch {
    case e: Exception =>
      // get it as a file (working in eclipse)
      parrotImage = new ImageIcon("/Users/al/Projects/Scala/BlueParrot/src/main/resources/com/alvinalexander/blueparrot/blueparrot.png")
  }
  //val parrotImage = new ImageIcon(getClass.getResource("blueparrot.png"))
  //val parrotImage = new ImageIcon("/Users/al/Projects/Scala/BlueParrot/src/main/resources/com/alvinalexander/blueparrot/blueparrot.png")
  val parrotLabel = new JLabel()
  parrotLabel.setIcon(parrotImage)
  add(parrotLabel)
  setBackground(MainFrame.colorWhenNotRunning)
}

class ActionPanel extends JPanel {
  
  val editSoundFileFolderWidget = new JButton("Sound File Folder")
  val editPhrasesWidget = new JButton("Text Phrases")
  val editMaxWaitTimeWidget = new JButton("Max Wait Time")
  val startStopButton = new JButton("Start")
  val volumeSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 50)
  
  def configureVolumeSliderControl {
    volumeSlider.setToolTipText("Volume control");
    //volumeSlider.addChangeListener(controller);
    volumeSlider.setMajorTickSpacing(10);
    volumeSlider.setMinorTickSpacing(2);
    volumeSlider.setPaintTicks(true);
    volumeSlider.setPaintLabels(true);
    //volumeSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
    
    val labelTable = new Hashtable[Integer, JLabel]()
    labelTable.put(new Integer(0), new JLabel("Quiet"))
    labelTable.put(new Integer(100), new JLabel("Loud"))
    volumeSlider.setLabelTable(labelTable)
  }

  // configure the button panel
  val buttonPanel = new JPanel
  buttonPanel.setLayout(new FlowLayout)
  buttonPanel.add(startStopButton)
  buttonPanel.add(editSoundFileFolderWidget)
  buttonPanel.add(editPhrasesWidget)
  buttonPanel.add(editMaxWaitTimeWidget)

  // configure the slider panel
  configureVolumeSliderControl
  val sliderPanel = new JPanel
  sliderPanel.setLayout(new FlowLayout)
  sliderPanel.add(volumeSlider)

  // add the buttonPanel and sliderPanel to the main/complete panel
  val gridLayout = new GridLayout(0,2)
  setLayout(new FlowLayout)
  add(buttonPanel)
  add(sliderPanel)
}















