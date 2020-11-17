package me.friwi.jcefmaven.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Util to hash files (for the repository checksums)
 *
 * @author Fritz Windisch
 */
public class FileHasher {
    private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
    private static MessageDigest md5, sha1 = null;

    public static void createHashes(File file) throws IOException {
        try {
            if (md5 == null) md5 = MessageDigest.getInstance("MD5");
            if (sha1 == null) sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithms not found!", e);
        }
        hashFile(file, new File(file.getParentFile(), file.getName() + ".md5"), md5);
        hashFile(file, new File(file.getParentFile(), file.getName() + ".sha1"), sha1);
    }

    private static void hashFile(File in, File out, MessageDigest md) throws IOException {
        FileInputStream fis = new FileInputStream(in);
        byte[] buff = new byte[4096];
        int r;
        while ((r = fis.read(buff)) != -1) {
            md.update(buff, 0, r);
        }
        fis.close();
        out.getParentFile().mkdirs();
        out.createNewFile();
        PrintWriter fos = new PrintWriter(new FileOutputStream(out));
        fos.print(bytesToHex(md.digest()));
        fos.flush();
        fos.close();
    }

    private static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
