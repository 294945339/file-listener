package com.tdr.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;

/**
 * @author: dj
 * @Date: 2018/12/22 10:35
 * @Description:
 */

public class FileDownloadUtil {

    private static final String fileLastName = ".tmt";

    /**
     * 文件传输中临时名字
     *
     * @param path
     * @return
     */
    public static String localFileIsDoing(String path) {
        return path + fileLastName;
    }

    /**
     * 文件传输结束正式名字
     *
     * @param oldName
     * @return
     */
    public static String getDoneName(String oldName) {
        return StrUtil.subBefore(oldName, fileLastName, true);
    }

    /**
     * 文件传输结束,本地文件重新命名
     *
     * @param path
     */
    public static void localFileIsDone(String path) {
        try {
            File file = new File(path);
            String oldName = file.getName();
            FileUtil.rename(file, getDoneName(oldName), false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
