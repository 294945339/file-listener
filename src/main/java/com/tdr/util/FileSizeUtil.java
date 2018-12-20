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

    public static BigDecimal GetFileSizeByMB(File file) {
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

    public static boolean isBigByMB() {
        boolean base = false;
        

        return base;
    }


}
