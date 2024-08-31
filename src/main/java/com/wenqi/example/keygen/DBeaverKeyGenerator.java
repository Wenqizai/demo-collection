package com.wenqi.example.keygen;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

/**
 * 修改版本号： 24.1
 * public key 替换 dbeaver\plugins\com.dbeaver.app.ultimate_24xxx.jar\keys\dbeaver-ue-public.key
 * @author liangwenqi
 * @date 2024/8/28
 */
public class DBeaverKeyGenerator {

    private static final String LICENSE_ID = "LINUX-DO";
    private static final String PRODUCT_ID = "dbeaver-ue";
    private static final String PRODUCT_VERSION = "24.1";
    private static final String OWNER_ID = "";
    private static final String OWNER_COMPANY = "China";
    private static final String OWNER_NAME = "Levi.u";
    private static final String OWNER_EMAIL = "support@dbeaver.com";

    public static void main(String[] args) throws Exception {
        KeyPair keyPair = generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println("--- PUBLIC KEY ---");
        System.out.println(formatResult(publicKey.getEncoded()));
        //System.out.println("--- PRIVATE KEY ---");
        //System.out.println(formatResult(privateKey.getEncoded()));
        System.out.println("--- LICENSE ---");
        System.out.println(formatResult(getData(privateKey)));
    }

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, SecureRandom.getInstance("SHA1PRNG"));
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] getData(Key key) throws Exception {
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(238);
        outBuffer.write((byte) 1);// licenseFormat
        outBuffer.write(String.format("%-16s", LICENSE_ID).getBytes());// licenseId
        outBuffer.write('U');// licenseType
        outBuffer.write(ByteBuffer.allocate(8).putLong(System.currentTimeMillis()).array());// licenseIssueTime
        outBuffer.write(ByteBuffer.allocate(8).putLong(System.currentTimeMillis()).array());// licenseStartTime
        outBuffer.write(ByteBuffer.allocate(8).putLong(LocalDateTime.now().plusYears(30).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).array());// licenseEndTime
        outBuffer.write(ByteBuffer.allocate(8).putLong(1024L).array());// flags
        outBuffer.write(String.format("%-16s", PRODUCT_ID).getBytes());// productId
        outBuffer.write(String.format("%-8s", PRODUCT_VERSION).getBytes());// productVersion
        outBuffer.write(String.format("%-16s", OWNER_ID).getBytes());// ownerId
        outBuffer.write(String.format("%-64s", OWNER_COMPANY).getBytes());// ownerCompany
        outBuffer.write(String.format("%-32s", OWNER_NAME).getBytes());// ownerName
        outBuffer.write(String.format("%-48s", OWNER_EMAIL).getBytes());// ownerEmail
        outBuffer.write((byte) 1);// yearsNumber
        outBuffer.write((byte) 0);// reserved1
        outBuffer.write(ByteBuffer.allocate(2).putShort((short) 1).array());// usersNumber
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(1, key);
        return cipher.doFinal(outBuffer.toByteArray());
    }

    public static String formatResult(byte[] dataByte) {
        return Base64.getEncoder().encodeToString(dataByte).replaceAll("(.{" + 76 + "})", "$1\n");
    }

}