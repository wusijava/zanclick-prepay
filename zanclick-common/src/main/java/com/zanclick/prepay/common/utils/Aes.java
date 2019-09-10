package com.zanclick.prepay.common.utils;

/**
 * @author duchong
 * @description
 * @date
 */
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class Aes {
    public Aes() {
    }

    private static String AES_RULE = "AES/ECB/PKCS5Padding";
    private static String CHARSET = "UTF-8";
    private static String AES = "AES";

    public static String Encrypt(String sSrc, String sKey) throws Exception {
        byte[] raw = sKey.getBytes(CHARSET);
        SecretKeySpec spec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(AES_RULE);
        cipher.init(1, spec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes(CHARSET));
        return (new Base64()).encodeToString(encrypted);
    }

    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            byte[] raw = sKey.getBytes(CHARSET);
            SecretKeySpec spec = new SecretKeySpec(raw, AES);
            Cipher cipher = Cipher.getInstance(AES_RULE);
            cipher.init(2, spec);
            byte[] encrypted1 = (new Base64()).decode(sSrc);

            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, CHARSET);
                return originalString;
            } catch (Exception var8) {
                return null;
            }
        } catch (Exception var9) {
            return null;
        }
    }
}

