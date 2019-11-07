package Backend;


import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
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
        String passPhrase = showInputPasswordDialog("Enter passphrase:");

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
        passPhrase += "SALTsaltSALTsaltSALTsaltSALTsalt";
        byte[] hashedPassPhrase = mdHashFunction.digest(passPhrase.getBytes());
        hashedPassPhraseKey = new SecretKeySpec(hashedPassPhrase, 0, 16, "AES");

        createFileIfNotExists();

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

    private static void createFileIfNotExists() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        File fileWithPasswords = new File(pathString);
        if (!fileWithPasswords.exists()) {
            fileWithPasswords.createNewFile();
            Cipher cipherEncrypt = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, hashedPassPhraseKey);

            byte[] utf8s = cipherEncrypt.doFinal("admin,,0,1,8".getBytes("UTF8"));
            FileOutputStream fileOutputStream = new FileOutputStream(fileWithPasswords);
            fileOutputStream.write(utf8s);
            fileOutputStream.flush();
            fileOutputStream.close();
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
        contextString = contextString.replaceAll("[^a-zA-Z0-9,;!@#$%^&*()-=+/|.<>`~ ]", "");
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
            oldPassword = showInputPasswordDialog("Confirm old password");
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
            newPassword = showInputPasswordDialog("Choose password");
            if (newPassword == null) {
                return "";
            }
        }

        String confirmedPassword = "";
        while (isBadPassword(confirmedPassword) || !confirmedPassword.equals(newPassword)) {
            confirmedPassword = showInputPasswordDialog("Confirm password");
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
                    byte[] utf8s = getFormattedContext(userContext).getBytes("UTF8");
                    byte[] formattedEncryptedContext = cipherEncrypt.doFinal(utf8s);
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
        return String.format("%s,%s,%d,%d,%d\n", userContext.getUserName(), userContext.getPassword(), isBlocked, isLimited, userContext.getMinimumPasswordLength());
    }

    private static String showInputPasswordDialog(String message) {
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

    private static String getInfo() {
        String username = System.getProperty("user.name");
        String hostname = getHostname();
        String winDirectory = System.getenv("WINDIR");
        String system32 = winDirectory + "\\system32";
        int numberMouseOfButtons = MouseInfo.getNumberOfButtons();
        double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        long diskSpace = getDiskSpace();
        return username +
                hostname +
                winDirectory +
                system32 +
                numberMouseOfButtons +
                screenHeight +
                diskSpace;
    }

    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            return "";
        }
    }

    private static long getDiskSpace() {
        File file = new File("/");
        return file.getTotalSpace();
    }

    static void checkLicence() {
        String registryKey = showInputDialog("Enter registry key:");
        if (registryKey.isEmpty()) {
            System.exit(-1);
        }

        String userdir = System.getProperty("user.dir");
        File publicKeyFile = new File(userdir + "\\publicKey.txt");
        if (!publicKeyFile.exists()) {
            JOptionPane.showMessageDialog(null, "File publicKey.txt not found!");
            System.exit(-1);
        }

        try {
            String registryValueByKey = getRegistryValueByKey(registryKey);
            byte[] encryptedInfo = Base64.getDecoder().decode(registryValueByKey);

            byte[] bytes = Files.readAllBytes(publicKeyFile.toPath());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedInfo = cipher.doFinal(encryptedInfo);

            String info = getInfo();
            MessageDigest mdHashFunction = MessageDigest.getInstance("MD2");
            byte[] digest = mdHashFunction.digest(info.getBytes());

            if (!Arrays.equals(removeLeadingZeros(decryptedInfo), digest)) {
                JOptionPane.showMessageDialog(null, "Please check your licence!");
                System.exit(-1);
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(-1);
        }
    }

    private static byte[] removeLeadingZeros(byte[] arr) {
        int zeros = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                zeros = i;
                break;
            }
        }
        return Arrays.copyOfRange(arr, zeros, arr.length);
    }

    private static String getRegistryValueByKey(String registryKey) {
        String registryValue = "";
        try {
            registryValue = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, "Software", registryKey);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Пожалуйста, проверьте имя раздела!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        return registryValue;
    }

    private static String showInputDialog(String message) {
        JTextField jTextField = new JTextField();
        int okCxl = JOptionPane.showConfirmDialog(null, jTextField, message, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            return jTextField.getText();
        } else {
            System.exit(-1);
            return "";
        }

    }
}