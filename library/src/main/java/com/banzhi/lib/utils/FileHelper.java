package com.banzhi.lib.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * <pre>
 * author : jiang
 * time : 2017/4/18.
 * desc : 文件信息帮助类
 * </pre>
 */

public class FileHelper {
    public static final int SIZETYPE_B = 1;
    public static final int SIZETYPE_KB = 2;
    public static final int SIZETYPE_MB = 3;
    public static final int SIZETYPE_GB = 4;

    public static String getFileSizeStr(String path) {
        File file = new File(path);
        long fileSize = 0;
        fileSize = getFileSize(file);
        return formatSize(fileSize);
    }

    public static long getFileSize(String path) {
        File file = new File(path);
        long fileSize = 0;
        fileSize = getFileSize(file);
        return fileSize;
    }

    public static long getFileSize(File file) {
        long fileSize = 0;
        try {
            if (file.isDirectory()) {//文件夹
                fileSize = computeFilesSize(file);
            } else {//文件
                fileSize = computeFileSize(file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileSize;
    }

    /**
     * 计算文件大小
     *
     * @param file
     * @return
     */
    public static long computeFileSize(File file) throws FileNotFoundException {
        long fileSize = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                fileSize = fis.available();
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSize;
    }

    /**
     * 计算文件夹大小
     *
     * @param file
     * @return
     */
    public static long computeFilesSize(File file) throws FileNotFoundException {
        long size = 0;
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isDirectory()) {
                size += computeFilesSize(file1);
            } else {
                size += computeFileSize(file1);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param size
     * @return
     */
    public static String formatSize(long size) {
        String result;
        DecimalFormat df = new DecimalFormat("#.00");//保留2位小数
        if (size < 1024) {//B
            result = formatSize(size, SIZETYPE_B) + "B";
        } else if (size < 1024 * 1024) {//KB
            result = formatSize(size, SIZETYPE_KB) + "KB";
        } else if (size < 1024 * 1024 * 1024) {//MB
            result = formatSize(size, SIZETYPE_MB) + "MB";
        } else if (size < 1024 * 1024 * 1024 * 1024) {//GB
            result = formatSize(size, SIZETYPE_GB) + "GB";
        } else {
            result = "0B";
        }
        return result;
    }

    /**
     * 将文件大小转换为指定格式
     *
     * @param size     文件大小
     * @param sizeType 格式 {@link #SIZETYPE_B} or
     *                 {@link #SIZETYPE_KB} or {@link #SIZETYPE_MB}
     *                 or {@link#SIZETYPE_GB}.
     */
    public static double formatSize(long size, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) size));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) size / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) size / (1024 * 1024)));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) size / (1024 * 1024 * 1024)));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }
}
