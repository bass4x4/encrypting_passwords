package UI;

import javax.swing.*;

public class PassphraseWindow {
    private JTextField minimumLength;
    private JTextField maximumLength;
    private JCheckBox specialSymbolsAllowed;
    private JCheckBox lowercaseAllowed;
    private JCheckBox uppercaseAllowed;
    private JCheckBox numbersAllowed;
    private JButton applyButton;
    private JPanel passphrasePanel;

    public JPanel getPassphrasePanel() {
        return passphrasePanel;
    }
}
