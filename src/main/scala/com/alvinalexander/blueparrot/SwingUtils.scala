package com.alvinalexander.blueparrot

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * TODO move this to a github project
 */
object SwingUtils {

  /**
   * @param jFrame
   * @param currentDirectory
   * @param title The title on the dialog ("Select a File" or "Select a Directory")
   * @return You can get the filename with fileDialog.getFile(),
   * and you can get the directory with fileDialog.getDirectory().
   * String filename = fileDialog.getDirectory() + System.getProperty("file.separator") + fileDialog.getFile();
   * 
   */
  def letUserChooseFile(jFrame: JFrame, currentDirectory: String, title: String): FileDialog = {
    val fileDialog = new FileDialog(jFrame)
    fileDialog.setModal(true)
    fileDialog.setMode(FileDialog.LOAD)
    fileDialog.setTitle(title)
    if (currentDirectory!=null && !currentDirectory.trim.equals("")) {
      fileDialog.setDirectory(currentDirectory)
    }
    fileDialog.setVisible(true)
    return fileDialog
  }

  /**
   * Returns the full path to the file, if a file was selected.
   * Otherwise it returns null.
   */
  def getCanonicalFilenameFromFileDialog(fileDialog: FileDialog): String = {
    if (fileDialog.getDirectory() == null) return null;
    if (fileDialog.getFile() == null) return null;
    // this line not needed on mac os x
    //return fileDialog.getDirectory() + System.getProperty("file.separator") + fileDialog.getFile();
    return fileDialog.getDirectory() + fileDialog.getFile();
  }

  def getFilenameFromFileDialog(fileDialog: FileDialog): String = {
    return fileDialog.getFile
  }

  def getDirectoryFromFileDialog(fileDialog: FileDialog): String = {
    return fileDialog.getDirectory
  }

  def displayErrorMessage(frame: JFrame, errorMessage: String) {
    // create a JTextArea
    val textArea = new JTextArea(6, 25);
    textArea.setText(errorMessage);
    textArea.setEditable(false);

    // wrap a scrollpane around it
    val scrollPane = new JScrollPane(textArea);

    // display them in a message dialog
    // TODO i don't know if this will work if this is null
    JOptionPane.showMessageDialog(frame, scrollPane);
  }
  
  def sleep(millis: Long) {
    try {
      Thread.sleep(millis);
    } catch {
      case e: InterruptedException => // nothing
    }
  }

  def getScreenHeight: Double = Toolkit.getDefaultToolkit.getScreenSize.getHeight
  def getScreenWidth: Double = Toolkit.getDefaultToolkit.getScreenSize.getWidth

  
}
