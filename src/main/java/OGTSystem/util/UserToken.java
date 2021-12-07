package OGTSystem.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class UserToken {

    /**
     * 随机生成一个在128-256位之内，由0-9，A-Z a-z组成的一个token字符串
     *
     * @return 随机token字符串
     */
    public String gen(){

        Random random = new Random();
        int minLength = 64;
        int maxLength = 128;
        int tokenLength =  random.nextInt(maxLength-minLength)+minLength;
        byte[] nonce = new byte[tokenLength];
        try {
            SecureRandom rand = SecureRandom.getInstance("DRBG");
            rand.nextBytes(nonce);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        String token  = convertBytesToHex(nonce);
        return token;
    }

    private static String convertBytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte temp : bytes) {
            result.append(String.format("%02x", temp));
        }
        String token = "";
        token = result.toString();
        return token;
    }

}
