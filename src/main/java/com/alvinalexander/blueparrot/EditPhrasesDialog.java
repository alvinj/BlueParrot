package com.alvinalexander.blueparrot;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

public class EditPhrasesDialog extends JDialog {
  public EditPhrasesDialog(Frame owner) {
    super(owner);
    initComponents();
  }

  public EditPhrasesDialog(Dialog owner) {
    super(owner);
    initComponents();
  }

  public JLabel getHeaderLabel() {
    return headerLabel;
  }

  public JLabel getHelpText() {
    return helpText;
  }

  public JScrollPane getScrollPane() {
    return scrollPane;
  }

  public JTextArea getTextArea() {
    return textArea;
  }

  public JButton getOkButton() {
    return okButton;
  }

  public JButton getCancelButton() {
    return cancelButton;
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
    dialogPane = new JPanel();
    contentPanel = new JPanel();
    headerLabel = new JLabel();
    helpText = new JLabel();
    scrollPane = new JScrollPane();
    textArea = new JTextArea();
    textArea.setRows(15);
    textArea.setColumns(40);
    buttonBar = new JPanel();
    okButton = new JButton();
    cancelButton = new JButton();
    CellConstraints cc = new CellConstraints();

    //======== this ========
    setTitle("Parrot Phrases");
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    //======== dialogPane ========
    {
      dialogPane.setBorder(Borders.DIALOG_BORDER);
      dialogPane.setLayout(new BorderLayout());

      //======== contentPanel ========
      {
        contentPanel.setLayout(new FormLayout(
          new ColumnSpec[] {
            FormFactory.DEFAULT_COLSPEC,
            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            FormFactory.DEFAULT_COLSPEC,
            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
          },
          new RowSpec[] {
            FormFactory.DEFAULT_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC,
            FormFactory.DEFAULT_ROWSPEC,
            FormFactory.LINE_GAP_ROWSPEC,
            new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
          }));

        //---- headerLabel ----
        headerLabel.setText("Phrases the Parrot Should Speak");
        contentPanel.add(headerLabel, cc.xywh(1, 1, 5, 1));

        //---- helpText ----
        helpText.setText("Lorem ipsum some other things, yada yada ...");
        contentPanel.add(helpText, cc.xywh(1, 3, 5, 1));

        //======== scrollPane ========
        {
          scrollPane.setViewportView(textArea);
        }
        contentPanel.add(scrollPane, cc.xywh(1, 5, 5, 3));
      }
      dialogPane.add(contentPanel, BorderLayout.CENTER);

      //======== buttonBar ========
      {
        buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
        buttonBar.setLayout(new FormLayout(
          new ColumnSpec[] {
            FormFactory.GLUE_COLSPEC,
            FormFactory.BUTTON_COLSPEC,
            FormFactory.RELATED_GAP_COLSPEC,
            FormFactory.BUTTON_COLSPEC
          },
          RowSpec.decodeSpecs("pref")));

        //---- okButton ----
        okButton.setText("OK");
        buttonBar.add(okButton, cc.xy(2, 1));

        //---- cancelButton ----
        cancelButton.setText("Cancel");
        buttonBar.add(cancelButton, cc.xy(4, 1));
      }
      dialogPane.add(buttonBar, BorderLayout.SOUTH);
    }
    contentPane.add(dialogPane, BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization  //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
  private JPanel dialogPane;
  private JPanel contentPanel;
  private JLabel headerLabel;
  private JLabel helpText;
  private JScrollPane scrollPane;
  private JTextArea textArea;
  private JPanel buttonBar;
  private JButton okButton;
  private JButton cancelButton;
  // JFormDesigner - End of variables declaration  //GEN-END:variables
}


