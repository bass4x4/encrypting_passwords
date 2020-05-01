package Interface;

import Backend.PasswordUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CipherWindow extends JFrame {
    private JButton cipherTextButton;
    private JButton chooseFileButton;
    private JButton decipherTextButton;
    private JPanel cipherPanel;
    private JButton saveToFileButton;
    private JCheckBox eraseFileAfterEncrypt;
    private JTextArea textToEditField;
    private JTextArea resultTextField;
    private JButton cipherFileButton;
    private JButton decipherFileButton;
    private JLabel pathLabel;
    private JLabel statusLabel;

    private static String FILE_TO_CIPHER_PATH = "";

    public CipherWindow() {
        statusLabel = statusLabel();

        cipherTextButton.addActionListener(actionEvent -> resultTextField.setText(PasswordUtils.cesarCipher(textToEditField.getText())));
        decipherTextButton.addActionListener(actionEvent -> resultTextField.setText(PasswordUtils.cesarDecipher(textToEditField.getText())));

        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getPath();
                FILE_TO_CIPHER_PATH = path;
                JOptionPane.showMessageDialog(null, "������ ����:" + path);
                pathLabel.setText(path.length() > 30 ? "..." + path.substring(path.length() - 30) : path);
            }
        });

        cipherFileButton.addActionListener(actionEvent -> {
            File file = new File(FILE_TO_CIPHER_PATH);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, String.format("���� �� ����������: %s", FILE_TO_CIPHER_PATH));
            } else {
                try {
                    String folderPath = chooseFolder();
                    if (folderPath.isEmpty() || !Files.exists(Paths.get(folderPath))) {
                        JOptionPane.showMessageDialog(null, "����� �� ���� ������� ���� ������ �������� ����!");
                    } else {
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        String result = PasswordUtils.cesarCipher(new String(bytes));
                        if (eraseFileAfterEncrypt.isSelected()) {
                            Files.delete(file.toPath());
                            JOptionPane.showMessageDialog(null, String.format("�������� ���� ��� ������� ������, �������� ��� ����� � �������� ��������� �������� ����������! %s", file.getPath()));
                        } else {
                            JOptionPane.showMessageDialog(null, "������� ��, �������� ��� ����� � �������� ��������� �������� ����������.");
                        }
                        String fileName = PasswordUtils.showInputNameDialog("�������� ��� �����(����� ����� ��������� ���������� .enc):", file.getName());
                        if (fileName != null && !fileName.isEmpty()) {
                            Path cipheredFilePath = Paths.get(folderPath + "\\" + fileName + ".enc");
                            Files.write(cipheredFilePath, result.getBytes());
                            JOptionPane.showMessageDialog(null, String.format("������������� ���� ������� ������� �: %s", cipheredFilePath));
                        } else {
                            JOptionPane.showMessageDialog(null, "�� �� ������� ��� �����.");
                        }
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "������ ������ ������ �� �����!");
                }
            }
        });
        decipherFileButton.addActionListener(actionEvent -> {
            File file = new File(FILE_TO_CIPHER_PATH);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, String.format("���� �� ����������: %s", FILE_TO_CIPHER_PATH));
            } else {
                try {
                    String folderPath = chooseFolder();
                    if (folderPath.isEmpty() || !Files.exists(Paths.get(folderPath))) {
                        JOptionPane.showMessageDialog(null, "����� �� ���� ������� ���� ������ �������� ����!");
                    } else {
                        byte[] bytes = Files.readAllBytes(file.toPath());
                        String fileName = PasswordUtils.showInputNameDialog("�������� ��� ����� (� �����������!):", file.getName());
                        if (fileName != null && !fileName.isEmpty()) {
                            Path decipheredFilePath = Paths.get(folderPath + "\\" + fileName);
                            Files.write(decipheredFilePath, PasswordUtils.cesarDecipher(new String(bytes)).getBytes());
                            JOptionPane.showMessageDialog(null, String.format("�������� ���� ������� ������� �: %s", decipheredFilePath));
                        } else {
                            JOptionPane.showMessageDialog(null, "�� �� ������� ��� �����.");
                        }
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "������ ������ ������ �� �����!");
                }
            }
        });
    }

    private JLabel statusLabel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(this.getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("������ ����:");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        return statusLabel;
    }

    private String chooseFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JOptionPane.showMessageDialog(null, "�������� ����� ��� ���������� �����.");
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getPath();
        }
        return "";
    }

    public JPanel getCipherPanel() {
        return cipherPanel;
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
        cipherPanel = new JPanel();
        cipherPanel.setLayout(new GridLayoutManager(10, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("���������:");
        cipherPanel.add(label1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("������ �����:");
        cipherPanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decipherTextButton = new JButton();
        decipherTextButton.setText("������������ �����");
        cipherPanel.add(decipherTextButton, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cipherTextButton = new JButton();
        cipherTextButton.setText("����������� �����");
        cipherPanel.add(cipherTextButton, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseFileButton = new JButton();
        chooseFileButton.setText("������� ����");
        cipherPanel.add(chooseFileButton, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eraseFileAfterEncrypt = new JCheckBox();
        eraseFileAfterEncrypt.setText("������� ���� ����� ����������");
        cipherPanel.add(eraseFileAfterEncrypt, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        cipherPanel.add(scrollPane1, new GridConstraints(1, 0, 8, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textToEditField = new JTextArea();
        scrollPane1.setViewportView(textToEditField);
        final JScrollPane scrollPane2 = new JScrollPane();
        cipherPanel.add(scrollPane2, new GridConstraints(1, 3, 8, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        resultTextField = new JTextArea();
        scrollPane2.setViewportView(resultTextField);
        cipherFileButton = new JButton();
        cipherFileButton.setText("����������� ��������� ����");
        cipherPanel.add(cipherFileButton, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decipherFileButton = new JButton();
        decipherFileButton.setText("������������ ��������� ����");
        cipherPanel.add(decipherFileButton, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("������ ����:");
        cipherPanel.add(label3, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathLabel = new JLabel();
        pathLabel.setText("");
        cipherPanel.add(pathLabel, new GridConstraints(9, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return cipherPanel;
    }

}
