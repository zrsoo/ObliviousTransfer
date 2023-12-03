package encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class EncryptUtils {
    private final static String ENCRYPT_UTILS = "ENCRYPT UTILS: ";

    Cipher cipher;
    IvParameterSpec ivParameterSpec;

    // Choose symmetric encryption algorithm
    protected static final String encryptionAlgorithm = "AES";
    protected static final String hashAlgorithm = "SHA3-256";

    public EncryptUtils() {
        this.ivParameterSpec = generateIv();
    }

    public static String digest(String input) {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

        MessageDigest md;

        try{
            md = MessageDigest.getInstance(hashAlgorithm);
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println(ENCRYPT_UTILS + "The specified hashing algorithm " + hashAlgorithm + " cannot be found");
            return null;
        }

        byte[] result = md.digest(inputBytes);
        return bytesToHex(result);
    }

    private static String bytesToHex(byte[] inputBytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for(byte b : inputBytes)
        {
            stringBuilder.append(String.format("%02x", b));
        }

        return stringBuilder.toString();
    }

    public String encrypt(String input, String key)
    {
        SecretKey secretKey = getKeyFromString(key);

        try{
            cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherText);
        }
        catch (Exception e)
        {
            System.out.println(ENCRYPT_UTILS + "Encryption error: " + e.getMessage());
            return null;
        }
    }

    public String decrypt(String cipherText, String key)
    {
        SecretKey secretKey = getKeyFromString(key);

        try{
            cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(plainText);
        }
        catch (Exception e)
        {
            System.out.println(ENCRYPT_UTILS + "Decryption error: " + e.getMessage());
            return null;
        }
    }

    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static SecretKey getKeyFromString(String hexKey)
    {
        int len = hexKey.length();
        byte[] keyBytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            keyBytes[i / 2] = (byte) ((Character.digit(hexKey.charAt(i), 16) << 4)
                    + Character.digit(hexKey.charAt(i+1), 16));
        }
        return new SecretKeySpec(keyBytes, "AES");
    }
}
