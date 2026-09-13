package ca.soccer1992.lavaproxy.utils;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.List;

public class EncryptionUtils {

    public static final List<String> PEM_RSA_PUBLIC_KEY_DESCRIPTOR =
            List.of("-----BEGIN RSA PUBLIC KEY-----", "-----END RSA PUBLIC KEY-----");
    public static final List<String> PEM_RSA_PRIVATE_KEY_DESCRIPTOR =
            List.of("-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----");
    private static final Base64.Encoder MIME_SPECIAL_ENCODER
            = Base64.getMimeEncoder(76, "\n".getBytes(StandardCharsets.UTF_8));

    public static String pemEncode(Key toEncode) {
        if (toEncode == null) throw new IllegalArgumentException("Key is null");
        List<String> encoder;
        if (toEncode instanceof PublicKey) {
            encoder = EncryptionUtils.PEM_RSA_PUBLIC_KEY_DESCRIPTOR;
        } else if (toEncode instanceof PrivateKey) {
            encoder = EncryptionUtils.PEM_RSA_PRIVATE_KEY_DESCRIPTOR;
        } else {
            throw new IllegalArgumentException("Invalid key type, recieved keytype " + toEncode.getClass().getName());
        }
        return encoder.getFirst() + "\n"
                + urlEncode(toEncode.getEncoded()) + "\n"
                + encoder.getLast() + "\n";
    }

public static String urlEncode(byte[] data) {
    return MIME_SPECIAL_ENCODER.encodeToString(data);
}

public static byte[] urlDecode(String toParse) {
    return Base64.getMimeDecoder().decode(toParse);
}

    public static String twosComplementHexdigest(byte[] digest) {
        return new BigInteger(digest).toString(16); // mojangs hash system is stupid
    }
    public static KeyPair createRsaPair(final int size) {
        try {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(size);
            return generator.generateKeyPair();
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to generate keypair", e);
        }
    }
    public static byte[] RSADecrypt(KeyPair keyPair, byte[] encrypted) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        return cipher.doFinal(encrypted);
    }
}
