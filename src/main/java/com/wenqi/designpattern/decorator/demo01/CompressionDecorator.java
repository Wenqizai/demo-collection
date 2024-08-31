package com.wenqi.designpattern.decorator.demo01;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * @author liangwenqi
 * @date 2023/6/12
 */
public class CompressionDecorator extends DataSourceDecorator {
    private int compLevel = 6;

    public CompressionDecorator(DataSource dataSource) {
        super(dataSource);
    }

    public int getCompLevel() {
        return compLevel;
    }

    public void setCompLevel(int compLevel) {
        this.compLevel = compLevel;
    }

    @Override
    public void writeData(String data) {
        super.writeData(compress(data));
    }

    @Override
    public String readData() {
        return decompress(super.readData());
    }


    private String compress(String dataStr) {
        final byte[] data = dataStr.getBytes();
        try {
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final DeflaterOutputStream dos = new DeflaterOutputStream(bout);
            dos.write(data);
            dos.close();
            bout.close();
            return Base64.getEncoder().encodeToString(bout.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decompress(String dataStr) {
        final byte[] data = Base64.getDecoder().decode(dataStr);
        try (final InputStream in = new ByteArrayInputStream(data);
             final InflaterInputStream iin = new InflaterInputStream(in);
             final ByteArrayOutputStream bout = new ByteArrayOutputStream(512)){
            int b;
            while ((b = iin.read()) != -1) {
                bout.write(b);
            }
            return new String(bout.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
