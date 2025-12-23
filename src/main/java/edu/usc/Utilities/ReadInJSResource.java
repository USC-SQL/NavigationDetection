package edu.usc.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadInJSResource {

    public ReadInJSResource() {

    }

    public String read(ReadInJSResource obj, String fileName) {
        InputStream inputStream = obj.getClass().getClassLoader().getResourceAsStream(fileName);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        String resultString = null;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            resultString = result.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultString;
    }
}
