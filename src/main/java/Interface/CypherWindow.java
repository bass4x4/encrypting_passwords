package Interface;

import Backend.PasswordUtils;
import Backend.RC4;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class CypherWindow extends JFrame {
    private JButton cypherTextButton;
    private JButton chooseFileButton;
    private JButton decypherTextButton;
    private JPanel cypherPanel;
    private JRadioButton eightBit;
    private JRadioButton sixteenBit;
    private JButton saveToFileButton;
    private JCheckBox eraseFileAfterEncrypt;
    private JButton setPassphraseButton;
    private JTextArea textToEditField;
    private JTextArea resultTextField;
    private JButton cypherFileButton;
    private JButton decypherFileButton;
    private final JLabel statusLabel;

    public CypherWindow() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(this.getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        this.statusLabel = new JLabel("������ ����:");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(sixteenBit);
        buttonGroup.add(eightBit);
        eightBit.setSelected(true);
        setPassphraseButton.addActionListener(actionEvent -> PasswordUtils.setNewConfirmedPassphrase());
        chooseFileButton.addActionListener(actionEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getPath();
                PasswordUtils.FILE_TO_CYPHER_PATH = path;
                JOptionPane.showMessageDialog(null, "������ ����:" + path);
            }
        });
        cypherTextButton.addActionListener(actionEvent -> {
            if (PasswordUtils.passphraseFitsRules()) {
                RC4 rc4 = new RC4(PasswordUtils.PASSPHRASE.getBytes(), eightBit.isSelected());
                String plainText = textToEditField.getText() + PasswordUtils.EXTRA_PASS_PART;
                byte[] bytes = rc4.Encode(plainText.getBytes());
                resultTextField.setText(new String(bytes));
            }
        });
        decypherTextButton.addActionListener(actionEvent -> {
            if (PasswordUtils.passphraseFitsRules()) {
                String passphrase = PasswordUtils.showInputDialog("������� ��������� �����:");
                if (passphrase == null) {
                    return;
                }
                RC4 rc4 = new RC4(passphrase.getBytes(), eightBit.isSelected());
                String plainText = resultTextField.getText();
                byte[] bytes = rc4.Encode(plainText.getBytes());
                String result = new String(bytes);
                try {
                    String extraPassPart = result.substring(result.length() - PasswordUtils.EXTRA_PASS_PART.length());
                    if (!extraPassPart.equals(PasswordUtils.EXTRA_PASS_PART)) {
                        JOptionPane.showMessageDialog(null, "������� �������� ��������� �����!");
                    } else {
                        textToEditField.setText(result.substring(0, result.length() - PasswordUtils.EXTRA_PASS_PART.length()));
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    JOptionPane.showMessageDialog(null, "��������� ������� �����!");
                }
            }
        });
        saveToFileButton.addActionListener(actionEvent -> {
            File file = new File(PasswordUtils.PATH + "\\EncodedText.txt");
            try {
                Files.write(file.toPath(), resultTextField.getText().getBytes());
                JOptionPane.showMessageDialog(null, String.format("������������� ����� ������� ������� � ����: %s", PasswordUtils.PATH + "\\EncodedText.txt"));
            } catch (IOException e) {
            }
        });
        cypherFileButton.addActionListener(actionEvent -> {
            File file = new File(PasswordUtils.FILE_TO_CYPHER_PATH);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, String.format("���� �� ����������: %s", PasswordUtils.FILE_TO_CYPHER_PATH));
            } else {
                if (PasswordUtils.passphraseFitsRules()) {
                    try {
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        byte[] plainText = concatByteArrays(bytes, PasswordUtils.EXTRA_PASS_PART.getBytes());
                        RC4 rc4 = new RC4(PasswordUtils.PASSPHRASE.getBytes(), eightBit.isSelected());
                        byte[] encodedText = rc4.Encode(plainText);
                        String cypheredFilePath = PasswordUtils.PATH + "\\" + file.getName();
                        File encodedFilePath = new File(cypheredFilePath);
                        Files.write(encodedFilePath.toPath(), encodedText);
                        JOptionPane.showMessageDialog(null, String.format("������������� ���� ������� ������� �: %s", cypheredFilePath));
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "������ ������ ������ �� �����!");
                    }
                }
            }
        });
        decypherFileButton.addActionListener(actionEvent -> {
            File file = new File(PasswordUtils.FILE_TO_CYPHER_PATH);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, String.format("���� �� ����������: %s", PasswordUtils.FILE_TO_CYPHER_PATH));
            } else {
                try {
                    String passphrase = PasswordUtils.showInputDialog("������� ��������� �����:");
                    if (passphrase == null) {
                        return;
                    }
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    RC4 rc4 = new RC4(passphrase.getBytes(), eightBit.isSelected());
                    byte[] result = rc4.Encode(bytes);
                    String extraPassPart = new String(Arrays.copyOfRange(result, result.length - PasswordUtils.EXTRA_PASS_PART.length(), result.length));
                    if (!extraPassPart.equals(PasswordUtils.EXTRA_PASS_PART)) {
                        JOptionPane.showMessageDialog(null, "������� �������� ��������� �����!");
                    } else {
                        byte[] resultFile = Arrays.copyOfRange(result, 0, result.length - PasswordUtils.EXTRA_PASS_PART.length());
                        String decypheredFilePath = PasswordUtils.PATH + "\\" + file.getName();
                        File decodedFilePath = new File(decypheredFilePath);
                        Files.write(decodedFilePath.toPath(), resultFile);
                        JOptionPane.showMessageDialog(null, String.format("�������� ���� ������� ������� �: %s", decypheredFilePath));
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "������ ������ ������ �� �����!");
                }
            }
        });
    }

    private byte[] concatByteArrays(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public JPanel getCypherPanel() {
        return cypherPanel;
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
        cypherPanel = new JPanel();
        cypherPanel.setLayout(new GridLayoutManager(14, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("������ S-Block-�");
        cypherPanel.add(label1, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        cypherPanel.add(spacer1, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("���������:");
        cypherPanel.add(label2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("������ �����:");
        cypherPanel.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decypherTextButton = new JButton();
        decypherTextButton.setText("������������ �����");
        cypherPanel.add(decypherTextButton, new GridConstraints(11, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cypherTextButton = new JButton();
        cypherTextButton.setText("����������� �����");
        cypherPanel.add(cypherTextButton, new GridConstraints(10, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseFileButton = new JButton();
        chooseFileButton.setText("������� ����");
        cypherPanel.add(chooseFileButton, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        cypherPanel.add(spacer2, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        cypherPanel.add(spacer3, new GridConstraints(13, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        saveToFileButton = new JButton();
        saveToFileButton.setText("��������� ����� � ����");
        cypherPanel.add(saveToFileButton, new GridConstraints(12, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eraseFileAfterEncrypt = new JCheckBox();
        eraseFileAfterEncrypt.setText("������� ���� ����� ����������");
        cypherPanel.add(eraseFileAfterEncrypt, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setPassphraseButton = new JButton();
        setPassphraseButton.setText("������ ��������� �����");
        cypherPanel.add(setPassphraseButton, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        cypherPanel.add(scrollPane1, new GridConstraints(2, 0, 11, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textToEditField = new JTextArea();
        scrollPane1.setViewportView(textToEditField);
        final JScrollPane scrollPane2 = new JScrollPane();
        cypherPanel.add(scrollPane2, new GridConstraints(2, 3, 11, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        resultTextField = new JTextArea();
        scrollPane2.setViewportView(resultTextField);
        sixteenBit = new JRadioButton();
        sixteenBit.setText("16 ���");
        cypherPanel.add(sixteenBit, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eightBit = new JRadioButton();
        eightBit.setText("8 ���");
        cypherPanel.add(eightBit, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cypherFileButton = new JButton();
        cypherFileButton.setText("����������� ����");
        cypherPanel.add(cypherFileButton, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decypherFileButton = new JButton();
        decypherFileButton.setText("������������ ����");
        cypherPanel.add(decypherFileButton, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        cypherPanel.add(spacer4, new GridConstraints(9, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return cypherPanel;
    }

}
