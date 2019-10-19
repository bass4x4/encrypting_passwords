package UI;

import javax.swing.*;

public class CypherWindow {
    private JButton cypherButton;
    private JButton chooseFileButton;
    private JButton decypherButton;
    private JTextField textToEditTextfield;
    private JTextField resultTextfield;
    private JPanel cypherPanel;
    private JRadioButton eightBit;
    private JRadioButton sixteenBit;
    private JPasswordField passwordField;
    private JButton saveToFIleButton;
    private JCheckBox eraseFileAfterEncrypt;

    public JPanel getCypherPanel() {
        return cypherPanel;
    }
}
