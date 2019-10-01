package Backend;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class PasswordUtils {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtils.class);

    public static final Map<String, UserContext> PASSWORDS = new HashMap<>();

    public static final String ADMIN_NAME = "admin";

    private static SecretKey hashedPassPhraseKey;

    public static UserContext currentUser = null;

    private static String pathString = System.getProperty("user.dir") + "\\PASSWORDS.txt";

    static void initPasswords() {
        String passPhrase = showInputDialog("Enter passphrase:");

        try {
            if (!checkHashedPassPhraseAndInitPasswords(passPhrase)) {
                System.exit(1);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(1);
        } catch (BadPaddingException e) {
            JOptionPane.showMessageDialog(null, "Wrong passphrase!");
            System.exit(1);
        } catch (InvalidPathException e) {
            JOptionPane.showMessageDialog(null, "File with passwords doesn't exist!");
            System.exit(1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading from file!");
            System.exit(1);
        }
    }

    private static boolean checkHashedPassPhraseAndInitPasswords(String passPhrase) throws NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, IOException {
        MessageDigest mdHashFunction = MessageDigest.getInstance("MD2");
        byte[] hashedPassPhrase = mdHashFunction.digest(passPhrase.getBytes());

        hashedPassPhraseKey = new SecretKeySpec(hashedPassPhrase, 0, 16, "AES");

        Cipher cipherDecrypt = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, hashedPassPhraseKey);

        Path passwordsPath = Paths.get(pathString);
        byte[] encryptedByteArray = Files.readAllBytes(passwordsPath);
        String decryptedString = new String(cipherDecrypt.doFinal(encryptedByteArray));
        String[] decryptedLines = decryptedString.split("\\r?\\n");
        if (!contextsHaveRightFormatParseThem(decryptedLines)) {
            return false;
        } else {
            if (adminExists()) {
                if (adminHasPassword()) {
                    return true;
                } else if (firstStart()) {
                    setUsersPassword(ADMIN_NAME);
                    return true;
                }
                return false;
            } else {
                return false;
            }
        }
    }

    private static boolean adminExists() {
        return PASSWORDS.containsKey(ADMIN_NAME);
    }

    private static boolean adminHasPassword() {
        return !PASSWORDS.get(ADMIN_NAME).getPassword().isEmpty();
    }

    private static boolean firstStart() {
        return PASSWORDS.get(ADMIN_NAME).getPassword().isEmpty() && PASSWORDS.size() == 1;
    }

    private static boolean contextsHaveRightFormatParseThem(String[] contextLines) {
        Stream<String> contextsStream = Stream.of(contextLines);
        return contextsStream.allMatch(PasswordUtils::parseContextFromLine);
    }

    private static boolean parseContextFromLine(String contextString) {
        String[] dividedContext = contextString.split(",");
        if (dividedContext.length != 5) {
            JOptionPane.showMessageDialog(null, "Check passphrase!");
            System.exit(1);
        }

        String userName = dividedContext[0];
        String isBlocked = dividedContext[2];
        String passwordAllowed = dividedContext[3];
        String minimumPasswordLength = dividedContext[4];

        if (userName.isEmpty() || isBlocked.isEmpty() || passwordAllowed.isEmpty() || minimumPasswordLength.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Check passphrase!");
            System.exit(1);
        }

        UserContext userContext = new UserContext(userName, dividedContext[1], isBlocked.equals("1"), passwordAllowed.equals("1"), Integer.parseInt(minimumPasswordLength));
        PASSWORDS.put(userName, userContext);
        return true;
    }

    public static void setUsersNewPassword(String userName) {
        String oldPassword = "";
        while (isBadPassword(oldPassword)) {
            oldPassword = showInputDialog("Confirm old password");
            if (oldPassword == null) {
                return;
            }
        }

        String decryptedPassword = PasswordUtils.getDecryptedPassword(userName);
        if (!oldPassword.equals(decryptedPassword)) {
            setUsersNewPassword(userName);
        } else {
            setUsersPassword(userName);
        }
    }

    public static void setUsersPassword(String userName) {
        String newPassword = getNewConfirmedPassword(userName);
        if (!newPassword.isEmpty()) {
            String encryptedPassword = PasswordUtils.getEncryptedPassword(userName, newPassword);
            PASSWORDS.get(userName).setPassword(encryptedPassword);
        } else {
            setUsersPassword(userName);
        }
    }

    private static String getNewConfirmedPassword(String userName) {
        String newPassword = "";
        while (isBadPassword(newPassword) || !passwordFitsRules(userName, newPassword)) {
            newPassword = showInputDialog("Choose password");
            if (newPassword == null) {
                return "";
            }
        }

        String confirmedPassword = "";
        while (isBadPassword(confirmedPassword) || !confirmedPassword.equals(newPassword)) {
            confirmedPassword = showInputDialog("Confirm password");
            if (confirmedPassword == null) {
                return "";
            }
        }

        return newPassword;
    }

    private static boolean passwordFitsRules(String userName, String password) {
        if (PASSWORDS.get(userName).isPasswordLimited()) {
            int minimumPasswordLength = PASSWORDS.get(userName).getMinimumPasswordLength();
            boolean passwordMatches = password.length() >= minimumPasswordLength;
            if (!passwordMatches) {
                JOptionPane.showMessageDialog(null, String.format("Minimum password length is %d!", minimumPasswordLength));
            }
            return passwordMatches;
        }
        return true;
    }

    static void savePasswordsToFile() {
        try {
            Cipher cipherEncrypt = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, hashedPassPhraseKey);

            FileOutputStream fileOutputStream = new FileOutputStream(pathString, false);
            PASSWORDS.values().forEach(userContext -> {
                try {
                    byte[] formattedEncryptedContext = cipherEncrypt.doFinal(getFormattedContext(userContext).getBytes("UTF8"));
                    fileOutputStream.write(formattedEncryptedContext);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Could't write context to file");
                } catch (BadPaddingException | IllegalBlockSizeException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            });
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File with passwords not found");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing to file");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private static String getFormattedContext(UserContext userContext) {
        int isBlocked = userContext.isBlocked() ? 1 : 0;
        int isLimited = userContext.isPasswordLimited() ? 1 : 0;
        return String.format("%s,%s,%d,%d,%d%n", userContext.getUserName(), userContext.getPassword(), isBlocked, isLimited, userContext.getMinimumPasswordLength());
    }

    private static String showInputDialog(String message) {
        JPasswordField passwordField = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, passwordField, message, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            return new String(passwordField.getPassword());
        } else {
            return null;
        }
    }

    private static boolean isBadPassword(String password) {
        return password.isEmpty() || password.contains(" ") || password.contains(",");
    }

    private static int[] getKey(String userNameToCut, String password) {
        String userName;
        if (userNameToCut.length() > password.length()) {
            userName = userNameToCut.substring(0, password.length());
        } else {
            userName = userNameToCut;
        }
        HashSet<Character> uniqueCharacters = new HashSet<>();
        for (int i = 0; i < userName.toCharArray().length; i++) {
            uniqueCharacters.add(userName.charAt(i));
        }

        ArrayList<Character> sortedCharacters = new ArrayList<>(uniqueCharacters);
        Collections.sort(sortedCharacters);

        AtomicInteger counter = new AtomicInteger(0);
        int[] key = new int[userName.length()];

        sortedCharacters.forEach(character -> {
            for (int i = 0; i < userName.toCharArray().length; i++) {
                if (userName.charAt(i) == character) {
                    key[i] = counter.getAndIncrement();
                }
            }
        });

        return key;
    }

    public static String getEncryptedPassword(String userName, String password) {
        int[] key = getKey(userName, password);
        char[] encryptedPassword = new char[password.length()];
        if (password.length() == key.length) {
            for (int i = 0; i < key.length; i++) {
                encryptedPassword[i] = password.charAt(key[i]);
            }
            return new String(encryptedPassword);
        } else {
            if (password.length() % key.length == 0) {
                return getEncryptedByPartsPassword(password, key, encryptedPassword);
            } else {
                String passwordWithExtraSpaces = password;
                int numberOfSpacesToAdd = key.length - (password.length() % key.length);
                for (int i = 0; i < numberOfSpacesToAdd; i++) {
                    passwordWithExtraSpaces += " ";
                }
                char[] encryptedPasswordWithExtraSpaces = new char[passwordWithExtraSpaces.length()];
                return getEncryptedByPartsPassword(passwordWithExtraSpaces, key, encryptedPasswordWithExtraSpaces);
            }
        }
    }

    private static String getEncryptedByPartsPassword(String password, int[] key, char[] encryptedPassword) {
        int numberOfParts = password.length() / key.length;
        for (int numberOfPart = 0; numberOfPart < numberOfParts; numberOfPart++) {
            encryptPartOfPassword(password, encryptedPassword, key, numberOfPart);
        }
        return new String(encryptedPassword);
    }

    private static void encryptPartOfPassword(String password, char[] encryptedPassword, int[] key, int numberOfPart) {
        int startPositionToEncrypt = numberOfPart * key.length;
        for (int i = startPositionToEncrypt; i < startPositionToEncrypt + key.length; i++) {
            int positionToEncryptFromZero = key[i - startPositionToEncrypt];
            encryptedPassword[i] = password.charAt(startPositionToEncrypt + positionToEncryptFromZero);
        }
    }

    private static String getDecryptedPassword(String userName) {
        String encryptedPassword = PasswordUtils.PASSWORDS.get(userName).getPassword();
        int[] key = getKey(userName, encryptedPassword);
        char[] decryptedPassword = new char[encryptedPassword.length()];

        if (encryptedPassword.length() == key.length) {
            for (int i = 0; i < key.length; i++) {
                decryptedPassword[key[i]] = encryptedPassword.charAt(i);
            }
            return new String(decryptedPassword);
        } else {
            String decryptedByPartsPassword = getDecryptedByPartsPassword(encryptedPassword, key, decryptedPassword);
            return decryptedByPartsPassword.trim();
        }
    }

    private static String getDecryptedByPartsPassword(String encryptedPassword, int[] key, char[] decryptedPassword) {
        int numberOfParts = encryptedPassword.length() / key.length;
        for (int numberOfPart = 0; numberOfPart < numberOfParts; numberOfPart++) {
            decryptPartOfPassword(encryptedPassword, decryptedPassword, key, numberOfPart);
        }
        return new String(decryptedPassword);
    }

    private static void decryptPartOfPassword(String encryptedPassword, char[] decryptedPassword, int[] key, int numberOfPart) {
        int startPositionToDecrypt = numberOfPart * key.length;
        for (int i = startPositionToDecrypt; i < startPositionToDecrypt + key.length; i++) {
            int startPositionToDecryptFromZero = startPositionToDecrypt + key[i - startPositionToDecrypt];
            decryptedPassword[startPositionToDecryptFromZero] = encryptedPassword.charAt(i);
        }
    }
}