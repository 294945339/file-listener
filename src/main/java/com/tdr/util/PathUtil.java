package com.tdr.util;

import cn.hutool.core.util.StrUtil;

/**
 * @author: dj
 * @Date: 2018/12/21 09:50
 * @Description:
 */

public class PathUtil {

    public static String pathTransformation(String path) {
        try {
            path = StrUtil.replace(path, "/", System.getProperty("file.separator"));
            path = StrUtil.replace(path, "\\", System.getProperty("file.separator"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String getFtpUploadPath(String filePath, String targetPath) {
        String path = StrUtil.subAfter(filePath, "upload", true);
        path = targetPath + path;
        path = PathUtil.pathTransformation(path);
        path = StrUtil.subBefore(path, System.getProperty("file.separator"), true);
        return path;
    }

    public static String getLocalUploadPath(String filePath, String targetPath) {
        String path = StrUtil.subAfter(filePath, "upload", true);
        path = targetPath + path;
        path = PathUtil.pathTransformation(path);
        return path;
    }

}
