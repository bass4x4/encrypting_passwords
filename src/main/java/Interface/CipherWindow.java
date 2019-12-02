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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

public class CipherWindow extends JFrame {
    private JButton cipherTextButton;
    private JButton chooseFileButton;
    private JButton decipherTextButton;
    private JPanel cipherPanel;
    private JRadioButton eightBit;
    private JRadioButton sixteenBit;
    private JButton saveToFileButton;
    private JCheckBox eraseFileAfterEncrypt;
    private JButton setPassphraseButton;
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
        buttonGroup.add(sixteenBit);
        buttonGroup.add(eightBit);
        eightBit.setSelected(true);

        setPassphraseButton.addActionListener(actionEvent -> PasswordUtils.setNewConfirmedPassphrase());
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
        cipherTextButton.addActionListener(actionEvent -> {
            if (PasswordUtils.passphraseFitsRules()) {
                RC4 rc4 = new RC4(PasswordUtils.PASSPHRASE.getBytes(), eightBit.isSelected());
                byte[] plainTextBytes = textToEditField.getText().getBytes();
                byte[] bytes = rc4.Encode(Base64.getEncoder().encode(concatByteArrays(plainTextBytes, PasswordUtils.EXTRA_PASS_PART.getBytes())));
                resultTextField.setText(new String(bytes));
            }
        });
        decipherTextButton.addActionListener(actionEvent -> {
            String passphrase = PasswordUtils.showInputDialog("Введите парольную фразу:");
            if (passphrase == null) {
                return;
            }
            RC4 rc4 = new RC4(passphrase.getBytes(), eightBit.isSelected());
            String plainText = textToEditField.getText();
            byte[] result = Base64.getDecoder().decode(rc4.Encode(plainText.getBytes()));
            try {
                String extraPassPart = new String(Arrays.copyOfRange(result, result.length - PasswordUtils.EXTRA_PASS_PART.length(), result.length));
                if (!extraPassPart.equals(PasswordUtils.EXTRA_PASS_PART)) {
                    JOptionPane.showMessageDialog(null, "Введена неверная парольная фраза!");
                } else {
                    resultTextField.setText(new String(Arrays.copyOfRange(result, 0, result.length - PasswordUtils.EXTRA_PASS_PART.length())));
                }
            } catch (StringIndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(null, "Проверьте иходный текст!");
            }
        });
        saveToFileButton.addActionListener(actionEvent -> {
            try {
                String folderPath = chooseFolder();
                if (folderPath.isEmpty() || !Files.exists(Paths.get(folderPath))) {
                    JOptionPane.showMessageDialog(null, "Папка не была выбрана либо выбран неверный путь!");
                } else {
                    Path path = Paths.get(folderPath + "\\Result.txt");
                    Files.write(path, resultTextField.getText().getBytes());
                    JOptionPane.showMessageDialog(null, String.format("Результат успешно записан в файл: %s", path.toString()));
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка записи информации в файл!");
            }
        });
        cipherFileButton.addActionListener(actionEvent -> {
            File file = new File(PasswordUtils.FILE_TO_CIPHER_PATH);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, String.format("Файл не существует: %s", PasswordUtils.FILE_TO_CIPHER_PATH));
            } else {
                if (PasswordUtils.passphraseFitsRules()) {
                    try {
                        String folderPath = chooseFolder();
                        if (folderPath.isEmpty() || !Files.exists(Paths.get(folderPath))) {
                            JOptionPane.showMessageDialog(null, "Папка не была выбрана либо выбран неверный путь!");
                        } else {
                            byte[] bytes = Files.readAllBytes(file.toPath());
                            byte[] plainText = concatByteArrays(bytes, PasswordUtils.EXTRA_PASS_PART.getBytes());
                            RC4 rc4 = new RC4(PasswordUtils.PASSPHRASE.getBytes(), eightBit.isSelected());
                            byte[] encodedText = rc4.Encode(plainText);
                            Path cipheredFilePath = Paths.get(folderPath + "\\" + file.getName());
                            if (eraseFileAfterEncrypt.isSelected()) {
                                Files.delete(file.toPath());
                                JOptionPane.showMessageDialog(null, String.format("Исходный файл был успешно удален, ожадайте окончания процесса шифрования! %s", file.getPath()));
                            } else {
                                JOptionPane.showMessageDialog(null, "Нажмите ОК, ожидайте окончания процесса шифрования.");
                            }
                            Files.write(cipheredFilePath, encodedText);
                            JOptionPane.showMessageDialog(null, String.format("Зашифрованный файл успешно записан в: %s", cipheredFilePath));
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Ошибка чтения данных из файла!");
                    }
                }
            }
        });
        decipherFileButton.addActionListener(actionEvent -> {
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
                        RC4 rc4 = new RC4(passphrase.getBytes(), eightBit.isSelected());
                        byte[] result = rc4.Encode(bytes);
                        String extraPassPart = new String(Arrays.copyOfRange(result, result.length - PasswordUtils.EXTRA_PASS_PART.length(), result.length));
                        if (!extraPassPart.equals(PasswordUtils.EXTRA_PASS_PART)) {
                            JOptionPane.showMessageDialog(null, "Введена неверная парольная фраза!");
                        } else {
                            byte[] resultFile = Arrays.copyOfRange(result, 0, result.length - PasswordUtils.EXTRA_PASS_PART.length());
                            Path decipheredFilePath = Paths.get(folderPath + "\\" + file.getName());
                            Files.write(decipheredFilePath, resultFile);
                            JOptionPane.showMessageDialog(null, String.format("Исходный файл успешно записан в: %s", decipheredFilePath));
                        }
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Ошибка чтения данных из файла!");
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

    JPanel getCipherPanel() {
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
        cipherPanel.setLayout(new GridLayoutManager(15, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Размер S-Block-а:");
        cipherPanel.add(label1, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        cipherPanel.add(spacer1, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Результат:");
        cipherPanel.add(label2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Ввести текст:");
        cipherPanel.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decipherTextButton = new JButton();
        decipherTextButton.setText("Расшифровать текст");
        cipherPanel.add(decipherTextButton, new GridConstraints(11, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cipherTextButton = new JButton();
        cipherTextButton.setText("Зашифровать текст");
        cipherPanel.add(cipherTextButton, new GridConstraints(10, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chooseFileButton = new JButton();
        chooseFileButton.setText("Выбрать файл");
        cipherPanel.add(chooseFileButton, new GridConstraints(6, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        cipherPanel.add(spacer2, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        cipherPanel.add(spacer3, new GridConstraints(13, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        saveToFileButton = new JButton();
        saveToFileButton.setText("Записать результат в файл");
        cipherPanel.add(saveToFileButton, new GridConstraints(12, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eraseFileAfterEncrypt = new JCheckBox();
        eraseFileAfterEncrypt.setText("Стереть файл после шифрования");
        cipherPanel.add(eraseFileAfterEncrypt, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        setPassphraseButton = new JButton();
        setPassphraseButton.setText("Задать парольную фразу");
        cipherPanel.add(setPassphraseButton, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        cipherPanel.add(scrollPane1, new GridConstraints(2, 0, 11, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textToEditField = new JTextArea();
        scrollPane1.setViewportView(textToEditField);
        final JScrollPane scrollPane2 = new JScrollPane();
        cipherPanel.add(scrollPane2, new GridConstraints(2, 3, 11, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        resultTextField = new JTextArea();
        scrollPane2.setViewportView(resultTextField);
        sixteenBit = new JRadioButton();
        sixteenBit.setText("16 бит");
        cipherPanel.add(sixteenBit, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eightBit = new JRadioButton();
        eightBit.setText("8 бит");
        cipherPanel.add(eightBit, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cipherFileButton = new JButton();
        cipherFileButton.setText("Зашифровать выбранный файл");
        cipherPanel.add(cipherFileButton, new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decipherFileButton = new JButton();
        decipherFileButton.setText("Расшифровать выбранный файл");
        cipherPanel.add(decipherFileButton, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        cipherPanel.add(spacer4, new GridConstraints(9, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Выбран файл:");
        cipherPanel.add(label4, new GridConstraints(14, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathLabel = new JLabel();
        pathLabel.setText("");
        cipherPanel.add(pathLabel, new GridConstraints(14, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return cipherPanel;
    }

}
