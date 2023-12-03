package encrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils {
    private final static String ENCRYPT_UTILS = "ENCRYPT UTILS: ";

    public static String digest(String input, String algorithm)
    {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

        MessageDigest md;

        try{
            md = MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println(ENCRYPT_UTILS + "The specified encryption algorithm " + algorithm + " cannot be found");
            return null;
        }

        byte[] result = md.digest(inputBytes);
        return bytesToHex(result);
    }

    private static String bytesToHex(byte[] inputBytes)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for(byte b : inputBytes)
        {
            stringBuilder.append(String.format("%02x", b));
        }

        return stringBuilder.toString();
    }
}
