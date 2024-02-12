package com.example.webview_s;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class Utils {
    public static String calculateSHA1(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
