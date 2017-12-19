package org.knight.websocketchat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Resource {

    public File getFile(){
        InputStream inputStream = this.getClass().getResourceAsStream("/index.html");
        File file = new File("txt");

        inputstreamtofile(inputStream, file);
        return file;
    }

    /**
     * InputStream转为File
     * @param ins
     * @param file
     */
    public static void inputstreamtofile(InputStream ins,File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}