package com.tdr.service;

import cn.hutool.core.io.FileUtil;
import com.tdr.util.FileSizeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;

/**
 * @author dj
 */

@Service
public class LocalFileService {

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
        FileUtil.del(localFilePath);
    }

}
