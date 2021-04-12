import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 文件精确对比
 */
public class AutoCheckText {
    private static final String FILE1 = "/Users/wuyh/Desktop/FTP/A/b.text";
    private static final String FILE2 = "/Users/wuyh/Desktop/FTP/C/991447a2-2418-4957-90ea-ec33c865027c";


    public static void main(String args[]) throws IOException{
        FileInputStream file1 = new FileInputStream(FILE1);
        FileInputStream file2 = new FileInputStream(FILE2);
        FileChannel a = file1.getChannel();
        FileChannel b = file2.getChannel();
        // 创建字节接收区
        ByteBuffer buffer1 = ByteBuffer.allocate(20480);
        ByteBuffer buffer2 = ByteBuffer.allocate(20480);
        long fileSize1 = a.size();
        long fileSize2 = b.size();
        System.out.println("1. size: " + fileSize1);
        System.out.println("2. size: " + fileSize2);
        int i = 0;
        long total = (fileSize1 / 20480) + 1;
        while (i < total) {
            i++;
            if (i == total) {
                buffer1 = ByteBuffer.allocate((int) fileSize1 % 20480);
                buffer2 = ByteBuffer.allocate((int) fileSize1 % 20480);
            }
            a.read(buffer1);
            b.read(buffer2);
            buffer1.flip();
            buffer2.flip();
            String text1 = new String(buffer1.array());
            String text2 = new String(buffer2.array());
            if (!text1.equals(text2)){
                System.out.println(i);
                System.out.println(text1);
                System.out.println(text2);
                return;
            }
            buffer1.clear();
            buffer2.clear();
        }
        System.out.println("字节精确匹配成功 biu特否！");
    }
}
