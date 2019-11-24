package Interface;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

public class PassphraseWindow {
    private JCheckBox specialSymbolsAllowedCheckBox;
    private JCheckBox lowercaseAllowedCheckBox;
    private JCheckBox uppercaseAllowedCheckBox;
    private JCheckBox numbersAllowedCheckBox;
    private JButton applyButton;
    private JSpinner minLengthSpinner;
    private JSpinner maxLengthSpinner;
    private JPanel passphrasePanel;

    public static int minimumPassphraseLength = 1;
    public static int maximumPassphraseLength = 1;
    public static boolean specialSymbolsAllowed;
    public static boolean lowerCaseAllowed;
    public static boolean upperCaseAllowed;
    public static boolean numbersAllowed;

    public PassphraseWindow() {
        minLengthSpinner.setModel(new SpinnerNumberModel(1, 1, 32, 1.0));
        maxLengthSpinner.setModel(new SpinnerNumberModel(1, 1, 32, 1.0));
        applyButton.addActionListener(actionEvent -> {
            try {
                int minimumLength = (Integer) minLengthSpinner.getValue();
                int maximumLength = (Integer) maxLengthSpinner.getValue();
                if (maximumLength < minimumLength) {
                    JOptionPane.showMessageDialog(null, "������������ ����� ����� ������ �����������!");
                } else {
                    minimumPassphraseLength = minimumLength;
                    maximumPassphraseLength = maximumLength;
                    specialSymbolsAllowed = specialSymbolsAllowedCheckBox.isEnabled();
                    lowerCaseAllowed = lowercaseAllowedCheckBox.isEnabled();
                    upperCaseAllowed = uppercaseAllowedCheckBox.isEnabled();
                    numbersAllowed = numbersAllowedCheckBox.isEnabled();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "��������� ������������/����������� ����� �����");
            }
        });
    }

    public JPanel getPassphrasePanel() {
        return passphrasePanel;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        passphrasePanel = new JPanel();
        passphrasePanel.setLayout(new GridLayoutManager(13, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("����������� �����");
        passphrasePanel.add(label1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("������������ �����");
        passphrasePanel.add(label2, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        specialSymbolsAllowedCheckBox = new JCheckBox();
        specialSymbolsAllowedCheckBox.setText("������� ����������� ��������");
        passphrasePanel.add(specialSymbolsAllowedCheckBox, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lowercaseAllowedCheckBox = new JCheckBox();
        lowercaseAllowedCheckBox.setSelected(false);
        lowercaseAllowedCheckBox.setText("������� �������� ����");
        passphrasePanel.add(lowercaseAllowedCheckBox, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        uppercaseAllowedCheckBox = new JCheckBox();
        uppercaseAllowedCheckBox.setText("������� ��������� ����");
        passphrasePanel.add(uppercaseAllowedCheckBox, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        numbersAllowedCheckBox = new JCheckBox();
        numbersAllowedCheckBox.setText("������� ����");
        passphrasePanel.add(numbersAllowedCheckBox, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        passphrasePanel.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        passphrasePanel.add(spacer2, new GridConstraints(12, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        passphrasePanel.add(spacer3, new GridConstraints(1, 2, 9, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        passphrasePanel.add(spacer4, new GridConstraints(1, 0, 9, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        applyButton = new JButton();
        applyButton.setText("���������");
        passphrasePanel.add(applyButton, new GridConstraints(11, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        passphrasePanel.add(spacer5, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        passphrasePanel.add(spacer6, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        minLengthSpinner = new JSpinner();
        minLengthSpinner.setEnabled(true);
        passphrasePanel.add(minLengthSpinner, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        maxLengthSpinner = new JSpinner();
        passphrasePanel.add(maxLengthSpinner, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return passphrasePanel;
    }

}
