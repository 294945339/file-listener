package com.tdr.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author dj
 */

@Service
public class LocalFileService {

    private static final Log log = LogFactory.get();

    @Value("${ftp.filePath}")
    private String ftpFilePath;

    @Resource
    private FtpFileService ftpFileService;

    /**
     * 文件批量上传到ftp;并删除文件
     *
     * @param localFiles
     */
    public void fileUploadToFtpAndDelLocalFiles(File[] localFiles) {
        for (File file : localFiles) {
            this.fileUploadToFtpAndDelLocalFile(file);
        }
    }

    /**
     * 文件上传到ftp;并且删除文件
     *
     * @param file
     */
    public void fileUploadToFtpAndDelLocalFile(File file) {
        boolean base = fileUploadToFtp(file);
        if (base) {
            delLocalFile(file.getPath());
        }
    }

    /**
     * 文件上传到ftp
     *
     * @param localFile
     * @return
     */
    private boolean fileUploadToFtp(File localFile) {
        boolean base = false;
        try {
            if (FileUtil.isEmpty(localFile)) {
                return false;
            }
            base = ftpFileService.uploadFileFromProduction(ftpFilePath, localFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base;
    }

    /***
     * 删除文件
     * @param localFilePath
     */
    private void delLocalFile(String localFilePath) {
        try {
            FileUtil.del(localFilePath);
            log.info("删除文件成功:" + localFilePath);
        } catch (Exception e) {
            log.error("删除文件失败:" + localFilePath + ";原因:" + e);
        }
    }

}
