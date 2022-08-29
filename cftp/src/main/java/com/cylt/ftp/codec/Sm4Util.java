package com.cylt.ftp.codec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Security;

/**
 * 国密sm4加解密
 */
public class Sm4Util {
    public enum Algorithm {

        SM4("SM4","SM4","key长度：16 byte");

        private String keyAlgorithm;
        private String transformation;
        private String description;//描述
        Algorithm(String keyAlgorithm, String transformation, String description) {
            this.keyAlgorithm = keyAlgorithm;
            this.transformation = transformation;
            this.description = description;
        }
        public String getKeyAlgorithm() {
            return this.keyAlgorithm;
        }
        public String getTransformation() {
            return this.transformation;
        }
        public String getDescription() {
            return this.description;
        }
    }

    private static final String PROVIDER_NAME = "BC";//BouncyCastleProvider的名字
    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    /**
     * 自定字符串产生密钥
     * @param algorithm 加解密算法
     * @param keyStr 密钥字符串
     * @param charset 编码字符集
     * @return 密钥
     */
    public static SecretKey genKeyByStr(Algorithm algorithm, String keyStr, Charset charset) {
        return readKeyFromBytes(algorithm, keyStr.getBytes(charset));
    }

    /**
     * 根据指定字节数组产生密钥
     * @param algorithm 加解密算法
     * @param keyBytes 密钥字节数组
     * @return 密钥
     */
    public static SecretKey readKeyFromBytes(Sm4Util.Algorithm algorithm, byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, algorithm.getKeyAlgorithm());
    }

    /****************************加密*********************************/
    /**
     * 加密字符串，并进行base64编码
     * @param algorithm 加解密算法
     * @param key 密钥
     * @param data 明文
     * @param charset 编码字符集
     * @return 密文
     * @throws InvalidKeyException 密钥错误
     */
    public static String encryptBase64(Sm4Util.Algorithm algorithm, SecretKey key, String data, Charset charset) throws InvalidKeyException {
        return Base64.encodeBase64String(encrypt(algorithm, key, data.getBytes(charset)));
    }

    /**
     * 加密字节数组
     * @param algorithm 加解密算法
     * @param key 密钥
     * @param data 明文
     * @return 密文
     * @throws InvalidKeyException 密钥错误
     */
    public static byte[] encrypt(Sm4Util.Algorithm algorithm, SecretKey key, byte[] data) throws InvalidKeyException {
        try {
            return cipherDoFinal(algorithm, Cipher.ENCRYPT_MODE, key, data);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);//明文没有具体格式要求，不会出错。所以这个异常不需要外部捕获。
        }
    }

    /**
     * 加解密字节数组
     * @param algorithm 加解密算法
     * @param opmode 操作：1加密，2解密
     * @param key 密钥
     * @param data 数据
     * @throws InvalidKeyException 密钥错误
     * @throws BadPaddingException 解密密文错误(加密模式没有)
     */
    private static byte[] cipherDoFinal(Sm4Util.Algorithm algorithm, int opmode, SecretKey key, byte[] data) throws InvalidKeyException, BadPaddingException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(algorithm.getTransformation(), PROVIDER_NAME);
        } catch (Exception e) {
            //NoSuchAlgorithmException：加密算法名是本工具类提供的，如果错了业务没有办法处理。所以这个异常不需要外部捕获。
            //NoSuchProviderException：Provider是本工具类提供的，如果错了业务没有办法处理。所以这个异常不需要外部捕获。
            //NoSuchPaddingException：没有特定的填充机制，与环境有关，业务没有办法处理。所以这个异常不需要外部捕获。
            throw new RuntimeException(e);
        }
        cipher.init(opmode, key);
        try {
            return cipher.doFinal(data);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);//业务不需要将数据分块(好像由底层处理了)，如果错了业务没有办法处理。所以这个异常不需要外部捕获。
        }
    }

    /****************************解密*********************************/
    /**
     * 对字符串先进行base64解码，再解密
     * @param algorithm 加解密算法
     * @param key 密钥
     * @param data 密文
     * @param charset 编码字符集
     * @return 明文
     * @throws InvalidKeyException 密钥错误
     * @throws BadPaddingException 密文错误
     */
    public static String decryptBase64(Sm4Util.Algorithm algorithm, SecretKey key, String data, Charset charset)
            throws InvalidKeyException, BadPaddingException {
        return new String(decrypt(algorithm, key, Base64.decodeBase64(data)), charset);
    }

    /**
     * 解密字节数组
     * @param algorithm 加解密算法
     * @param key 密钥
     * @param data 密文
     * @return 明文
     * @throws InvalidKeyException 密钥错误
     * @throws BadPaddingException 密文错误
     */
    public static byte[] decrypt(Sm4Util.Algorithm algorithm, SecretKey key, byte[] data) throws InvalidKeyException, BadPaddingException {
        return cipherDoFinal(algorithm, Cipher.DECRYPT_MODE, key, data);
    }

    public static void main(String[] args) throws InvalidKeyException, BadPaddingException, IOException {

        Algorithm algorithm = Algorithm.SM4;
        //16位密钥字符串
        String encryptKey = "0123456789ABCDEF";
        //编码格式
        Charset encryptCharset = StandardCharsets.UTF_8;
        //生产密钥
        SecretKey key = genKeyByStr(algorithm, encryptKey, encryptCharset);
        //加密
        FileInputStream in = new FileInputStream("/Users/wuyh/Desktop/FTP/A/A.text");
        FileChannel inChannel = in.getChannel();
        //加密
        FileOutputStream ins = new FileOutputStream("/Users/wuyh/Desktop/FTP/A/b.text");
        FileChannel inChannels = ins.getChannel();
        // 取文件质量
        long fileSize = inChannel.size();
        // 创建字节接收区
        ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
        inChannel.read(buffer);
        String encryptBase64 = encryptBase64(algorithm, key, new String(buffer.array()), encryptCharset);
        System.out.println("encryptBase64 = " + encryptBase64);//encryptBase64=QtrH8m/aR9x/cySEoUb+Nw==
        inChannels.write(ByteBuffer.wrap(encryptBase64.getBytes()));
        //解密
        String decryptBase64 = decryptBase64(algorithm, key, encryptBase64, encryptCharset);
        System.out.println("decryptBase64 = " + decryptBase64);
    }
}
