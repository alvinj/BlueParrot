package com.devdaily.swingutils;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SwingUtils {

  /**
   * @param jFrame
   * @param currentDirectory
   * @return You can get the filename with fileDialog.getFile(),
   * and you can get the directory with fileDialog.getDirectory().
   * String filename = fileDialog.getDirectory() + System.getProperty("file.separator") + fileDialog.getFile();
   * 
   */
  public static FileDialog letUserChooseFile(JFrame jFrame, String currentDirectory)
  {
    FileDialog fileDialog = new FileDialog(jFrame);
    fileDialog.setModal(true);
    fileDialog.setMode(FileDialog.LOAD);
    fileDialog.setTitle("Open a File");
    if (currentDirectory!=null && !currentDirectory.trim().equals(""))
    {
      fileDialog.setDirectory(currentDirectory);
    }
    fileDialog.setVisible(true);
    return fileDialog;
  }

  /**
   * Returns the full path to the file, if a file was selected.
   * Otherwise it returns null.
   */
  public static String getCanonicalFilenameFromFileDialog(FileDialog fileDialog) {
    if (fileDialog.getDirectory() == null) return null;
    if (fileDialog.getFile() == null) return null;
    // this line not needed on mac os x
    //return fileDialog.getDirectory() + System.getProperty("file.separator") + fileDialog.getFile();
    return fileDialog.getDirectory() + fileDialog.getFile();
  }

  public static String getFilenameFromFileDialog(FileDialog fileDialog) {
    return fileDialog.getFile();
  }

  public static String getDirectoryFromFileDialog(FileDialog fileDialog) {
    return fileDialog.getDirectory();
  }

  public static void displayErrorMessage(JFrame frame, String errorMessage) {
    // create a JTextArea
    JTextArea textArea = new JTextArea(6, 25);
    textArea.setText(errorMessage);
    textArea.setEditable(false);

    // wrap a scrollpane around it
    JScrollPane scrollPane = new JScrollPane(textArea);

    // display them in a message dialog
    // TODO i don't know if this will work if this is null
    JOptionPane.showMessageDialog(frame, scrollPane);
  }
  
  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
    }
  }

  public static double getScreenHeight() {
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    return dim.getHeight();
  }
  
  public static double getScreenWidth() {
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    return dim.getWidth();
  }
  
}

