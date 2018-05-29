package com.littlemonkey.utils.gzip;


import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZip 工具类
 *
 * @author zhuanghd
 */
public abstract class GZipUtils {

    public static final int BUFFER = 1024;
    public static final String EXT = ".gz";

    /**
     * 数据压缩
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] compress(byte[] data) {
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        byte[] output = null;
        try {
            bais = new ByteArrayInputStream(data);
            baos = new ByteArrayOutputStream();
            // 压缩
            compress(bais, baos);
            output = baos.toByteArray();
            baos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            ;
        } finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(bais);
        }
        return output;
    }

    /**
     * 文件压缩
     *
     * @param file
     * @throws Exception
     */
    public static void compress(File file) throws Exception {
        compress(file, true);
    }

    /**
     * 文件压缩
     *
     * @param file
     * @param delete 是否删除原始文件
     * @throws Exception
     */
    public static void compress(File file, boolean delete) throws Exception {
        if (file == null) return;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(file.getPath() + EXT);
            compress(fis, fos);
            fos.flush();
            if (delete && file.exists()) {
                file.delete();
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }

    }

    /**
     * 数据压缩
     *
     * @param is
     * @param os
     * @throws Exception
     */
    public static void compress(InputStream is, OutputStream os)
            throws Exception {
        GZIPOutputStream gos = null;
        try {
            gos = new GZIPOutputStream(os);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = is.read(data, 0, BUFFER)) != -1) {
                gos.write(data, 0, count);
            }
            gos.finish();
            gos.flush();
        } catch (Exception e) {
            if (gos != null) {
                gos.close();
            }
        }
    }

    /**
     * 文件压缩
     *
     * @param path
     * @throws Exception
     */
    public static void compress(String path) throws Exception {
        compress(path, true);
    }

    /**
     * 文件压缩
     *
     * @param path
     * @param delete 是否删除原始文件
     * @throws Exception
     */
    public static void compress(String path, boolean delete) throws Exception {
        File file = new File(path);
        if (file != null) {
            compress(file, delete);
        }
    }

    /**
     * 数据解压缩
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decompress(byte[] data) throws Exception {
        ByteArrayInputStream byteArrayInputStream = null;
        ByteArrayOutputStream arrayOutputStream = null;
        byte[] result = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(data);
            arrayOutputStream = new ByteArrayOutputStream();
            // 解压缩
            decompress(byteArrayInputStream, arrayOutputStream);
            result = arrayOutputStream.toByteArray();
            arrayOutputStream.flush();
        } finally {
            if (arrayOutputStream != null) {
                arrayOutputStream.close();
            }
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
        }
        return result;
    }

    /**
     * 文件解压缩
     *
     * @param file
     * @throws Exception
     */
    public static void decompress(File file) throws Exception {
        if (file != null) {
            decompress(file, true);
        }
    }

    /**
     * 文件解压缩
     *
     * @param file
     * @param delete 是否删除原始文件
     * @throws Exception
     */
    public static void decompress(File file, boolean delete) throws Exception {
        if (file == null) return;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(file.getPath().replace(EXT, ""));
            decompress(fis, fos);
            fos.flush();
            if (delete) {
                file.delete();
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * 数据解压缩
     *
     * @param is
     * @param os
     * @throws Exception
     */
    public static void decompress(InputStream is, OutputStream os)
            throws Exception {

        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(is);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = gis.read(data, 0, BUFFER)) != -1) {
                os.write(data, 0, count);
            }
        } finally {
            if (gis != null) {
                gis.close();
            }
        }

    }

    /**
     * 文件解压缩
     *
     * @param path
     * @throws Exception
     */
    public static void decompress(String path) throws Exception {
        decompress(path, true);
    }

    /**
     * 文件解压缩
     *
     * @param path
     * @param delete 是否删除原始文件
     * @throws Exception
     */
    public static void decompress(String path, boolean delete) throws Exception {
        File file = new File(path);
        decompress(file, delete);
    }
}

