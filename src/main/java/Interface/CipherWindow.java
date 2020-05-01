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
    private final JLabel statusLabel;

    public CipherWindow() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(this.getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        this.statusLabel = new JLabel("Выбран файл:");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        ButtonGroup buttonGroup = new ButtonGroup();

        chooseFileButton.addActionListener(actionEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getPath();
                PasswordUtils.FILE_TO_CIPHER_PATH = path;
                JOptionPane.showMessageDialog(null, "Выбран файл:" + path);
                pathLabel.setText(path.length() > 30 ? "..." + path.substring(path.length() - 30) : path);
            }
        });
        cipherTextButton.addActionListener(actionEvent -> resultTextField.setText(PasswordUtils.cesarCipher(textToEditField.getText())));
        decipherTextButton.addActionListener(actionEvent -> resultTextField.setText(PasswordUtils.cesarDecipher(textToEditField.getText())));
        cipherFileButton.addActionListener(actionEvent -> {
            File file = new File(PasswordUtils.FILE_TO_CIPHER_PATH);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, String.format("Файл не существует: %s", PasswordUtils.FILE_TO_CIPHER_PATH));
            } else {
                /*if (PasswordUtils.passphraseFitsRules()) {
                    try {
                        String folderPath = chooseFolder();
                        if (folderPath.isEmpty() || !Files.exists(Paths.get(folderPath))) {
                            JOptionPane.showMessageDialog(null, "Папка не была выбрана либо выбран неверный путь!");
                        } else {
                            byte[] bytes = Files.readAllBytes(file.toPath());
                            byte[] plainText = concatByteArrays(bytes, PasswordUtils.EXTRA_PASS_PART.getBytes());
                            RC4 rc4 = new RC4(PasswordUtils.PASSPHRASE.getBytes(), eightBit.isSelected());
                            byte[] encodedText = rc4.Encode(Base64.getEncoder().encode(plainText));
                            if (eraseFileAfterEncrypt.isSelected()) {
                                Files.delete(file.toPath());
                                JOptionPane.showMessageDialog(null, String.format("Исходный файл был успешно удален, выберите имя файла и ожадайте окончания процесса шифрования! %s", file.getPath()));
                            } else {
                                JOptionPane.showMessageDialog(null, "Нажмите ОК, выберите имя файла и ожидайте окончания процесса шифрования.");
                            }
                            String fileName = PasswordUtils.showInputNameDialog("Выберите имя файла(после будет добавлено расширение .enc):", file.getName());
                            if (fileName != null && !fileName.isEmpty()) {
                                Path cipheredFilePath = Paths.get(folderPath + "\\" + fileName + ".enc");
                                Files.write(cipheredFilePath, encodedText);
                                JOptionPane.showMessageDialog(null, String.format("Зашифрованный файл успешно записан в: %s", cipheredFilePath));
                            } else {
                                JOptionPane.showMessageDialog(null, "Вы не выбрали имя файла.");
                            }
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Ошибка чтения данных из файла!");
                    }
                }*/
            }
        });
        decipherFileButton.addActionListener(actionEvent -> {
            try {
                File file = new File(PasswordUtils.FILE_TO_CIPHER_PATH);
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(null, String.format("Файл не существует: %s", PasswordUtils.FILE_TO_CIPHER_PATH));
                } else {
                    try {
                        String folderPath = chooseFolder();
                        if (folderPath.isEmpty() || !Files.exists(Paths.get(folderPath))) {
                            JOptionPane.showMessageDialog(null, "Папка не была выбрана либо выбран неверный путь!");
                        } else {
                            String passphrase = PasswordUtils.showInputDialog("Введите парольную фразу:");
                            if (passphrase == null) {
                                return;
                            }
                            byte[] bytes = Files.readAllBytes(file.toPath());
                            /*RC4 rc4 = new RC4(passphrase.getBytes(), eightBit.isSelected());
                            byte[] result = Base64.getDecoder().decode(rc4.Encode(bytes));
                            String extraPassPart = new String(Arrays.copyOfRange(result, result.length - PasswordUtils.EXTRA_PASS_PART.length(), result.length));
                            if (!extraPassPart.equals(PasswordUtils.EXTRA_PASS_PART)) {
                                JOptionPane.showMessageDialog(null, "Введена неверная парольная фраза!");
                            } else {
                                byte[] resultFile = Arrays.copyOfRange(result, 0, result.length - PasswordUtils.EXTRA_PASS_PART.length());
                                String fileName = PasswordUtils.showInputNameDialog("Выберите имя файла (с расширением!):", file.getName());
                                if (fileName != null && !fileName.isEmpty()) {
                                    Path decipheredFilePath = Paths.get(folderPath + "\\" + fileName);
                                    Files.write(decipheredFilePath, resultFile);
                                    JOptionPane.showMessageDialog(null, String.format("Исходный файл успешно записан в: %s", decipheredFilePath));
                                } else {
                                    JOptionPane.showMessageDialog(null, "Вы не выбрали имя файла.");
                                }
                            }*/
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Ошибка чтения данных из файла!");
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Введена неверная парольная фраза!");
            }
        });
    }

    private byte[] concatByteArrays(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    private String chooseFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JOptionPane.showMessageDialog(null, "Выберите папку для сохранения файла.");
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
        cipherPanel.setLayout(new GridLayoutManager(12, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Результат:");
        cipherPanel.add(label1, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Ввести текст:");
        cipherPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decipherTextButton = new JButton();
        decipherTextButton.setText("Расшифровать текст");
        cipherPanel.add(decipherTextButton, new GridConstraints(9, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cipherTextButton = new JButton();
        cipherTextButton.setText("Зашифровать текст");
        cipherPanel.add(cipherTextButton, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseFileButton = new JButton();
        chooseFileButton.setText("Выбрать файл");
        cipherPanel.add(chooseFileButton, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        cipherPanel.add(spacer1, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        cipherPanel.add(spacer2, new GridConstraints(10, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        eraseFileAfterEncrypt = new JCheckBox();
        eraseFileAfterEncrypt.setText("Стереть файл после шифрования");
        cipherPanel.add(eraseFileAfterEncrypt, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        cipherPanel.add(scrollPane1, new GridConstraints(2, 0, 8, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textToEditField = new JTextArea();
        scrollPane1.setViewportView(textToEditField);
        final JScrollPane scrollPane2 = new JScrollPane();
        cipherPanel.add(scrollPane2, new GridConstraints(2, 3, 8, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        resultTextField = new JTextArea();
        scrollPane2.setViewportView(resultTextField);
        cipherFileButton = new JButton();
        cipherFileButton.setText("Зашифровать выбранный файл");
        cipherPanel.add(cipherFileButton, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decipherFileButton = new JButton();
        decipherFileButton.setText("Расшифровать выбранный файл");
        cipherPanel.add(decipherFileButton, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        cipherPanel.add(spacer3, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Выбран файл:");
        cipherPanel.add(label3, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathLabel = new JLabel();
        pathLabel.setText("");
        cipherPanel.add(pathLabel, new GridConstraints(11, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return cipherPanel;
    }

}
