package com.tdr.service;

import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author dj
 * @Date: 2018/12/19 15:36
 * @Description: 第一次启动系统;触发方法
 */

@Service
public class FirstStartSysService {

    @Value("${sys.localFilePath}")
    private String localFilePath;

    @Resource
    private LocalFileService localFileService;

    public boolean go() {
        //读取文件夹下面所有文件
        //遍历上传和删除
        File folder = FileUtil.file(localFilePath);
        //判断文件夹是否为空
        if (!FileUtil.isEmpty(folder)) {
            //不是空;则查询出文件下面所有的文件
            File[] files = FileUtil.ls(localFilePath);
            localFileService.fileUploadToFtpAndDelLocalFiles(files);
        }
        return false;
    }
}

