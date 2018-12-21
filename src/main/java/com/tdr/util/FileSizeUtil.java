package com.tdr.util;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author: dj
 * @Date: 2018/12/19 16:31
 * @Description:
 */

public class FileSizeUtil {

    /**
     * 获取文件的大小(自动设置单位)
     *
     * @param file
     * @return
     */
    public static String GetFileSize(File file) {
        String size;
        if (file.exists() && file.isFile()) {
            long fileS = file.length();
            DecimalFormat df = new DecimalFormat("#.00");
            if (fileS < 1024) {
                size = df.format((double) fileS) + "BT";
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {
                size = df.format((double) fileS / 1048576) + "MB";
            } else {
                size = df.format((double) fileS / 1073741824) + "GB";
            }
        } else if (file.exists() && file.isDirectory()) {
            size = "";
        } else {
            size = "0BT";
        }
        return size;
    }

    /**
     * 获取文件的大小(单位为MB)
     *
     * @param file 文件
     * @return
     */
    public static BigDecimal getFileSizeByMB(File file) {
        BigDecimal sizeBig = new BigDecimal("0");
        try {
            if (file.exists() && file.isFile()) {
                long fileS = file.length();
                DecimalFormat df = new DecimalFormat("#.00");
                String size = df.format((double) fileS / 1048576);
                sizeBig = new BigDecimal(size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sizeBig;
    }

    /**
     * 根据double转换成mb
     *
     * @param fileSize
     * @return
     */
    public static BigDecimal getSizeByMB(double fileSize) {
        BigDecimal sizeBig = new BigDecimal("0");
        try {
            DecimalFormat df = new DecimalFormat("#.00");
            String size = df.format(fileSize / 1048576);
            sizeBig = new BigDecimal(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sizeBig;
    }

    /**
     * 比较2个文件的大小单位(MB)
     *
     * @return
     */
    public static boolean notIsBigByMB(String maxSize, String filePath) {
        boolean base = false;
        try {
            BigDecimal maxBig = new BigDecimal(maxSize);
            File file = new File(filePath);
            BigDecimal fileMaxBig = getFileSizeByMB(file);
            int bigCompare = maxBig.compareTo(fileMaxBig);
            if (0 > bigCompare) {
                base = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base;
    }

    /**
     * 判断double是否大于目标设置大小
     *
     * @param maxSize
     * @param fileSize
     * @return
     */
    public static boolean notIsBigByMB(String maxSize, double fileSize) {
        boolean base = false;
        try {
            BigDecimal maxBig = new BigDecimal(maxSize);
            BigDecimal fileMaxBig = getSizeByMB(fileSize);
            int bigCompare = maxBig.compareTo(fileMaxBig);
            if (0 > bigCompare) {
                base = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base;
    }

}
