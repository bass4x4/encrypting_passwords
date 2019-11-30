package Interface;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

public class AdminOptionsWindow {
    private JPanel adminOptionsPanel;
    private JButton cypherButton;
    private JButton passphraseSettingsButton;

    public AdminOptionsWindow() {
        cypherButton.addActionListener(actionEvent -> createPanelOfType(new CypherWindow().getCypherPanel(), 600, 350));
        passphraseSettingsButton.addActionListener(actionEvent -> createPanelOfType(new PassphraseWindow().getPassphrasePanel(), 300, 300));
    }

    private void createPanelOfType(JPanel adminOptionsPanel, int x, int y) {
        final JDialog optionWindow = new JDialog(new JFrame("Change password"), true);

        optionWindow.setContentPane(adminOptionsPanel);
        optionWindow.pack();
        optionWindow.setSize(x, y);
        optionWindow.setMinimumSize(new Dimension(200, 200));
        optionWindow.setVisible(true);
    }

    public JPanel getAdminOptionsPanel() {
        return adminOptionsPanel;
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
        adminOptionsPanel = new JPanel();
        adminOptionsPanel.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        adminOptionsPanel.add(spacer1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        passphraseSettingsButton = new JButton();
        passphraseSettingsButton.setText("��������� �����");
        adminOptionsPanel.add(passphraseSettingsButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        adminOptionsPanel.add(spacer2, new GridConstraints(1, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        adminOptionsPanel.add(spacer3, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        adminOptionsPanel.add(spacer4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cypherButton = new JButton();
        cypherButton.setText("����������");
        adminOptionsPanel.add(cypherButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return adminOptionsPanel;
    }

}
