package org.knight.websocketchat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 获取文件位置信息
 * @author Wayss
 * @date 2017/12/26
 */
public class Resource {

    private final static int bufferSize = 1000;

    /**
     * 获取聊天页面HTML
     * @return 聊天HTML的File对象
     */
    public File getIndexFile(){
        //获取文件流
        InputStream inputStream = this.getClass().getResourceAsStream("/index.html");
        File file = new File("txt");

        inputstreamtofile(inputStream, file);
        return file;
    }

    /**
     * InputStream转为File
     * @param ins 输入流
     * @param file 输出文件对象
     */
    public static void inputstreamtofile(InputStream ins,File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[bufferSize];
            //从输入流读入写到输出流中
            while ((bytesRead = ins.read(buffer, 0, bufferSize)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}