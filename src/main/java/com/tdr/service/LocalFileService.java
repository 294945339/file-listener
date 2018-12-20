package com.tdr.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @author dj
 */

@Service
public class LocalFileService {

    private static final Log log = LogFactory.get();

    @Resource
    private FtpFileService ftpFileService;

    /**
     * 文件批量上传到ftp;并删除文件
     *
     * @param localFiles
     */
    public void fileUploadToFtpAndDelLocalFiles(List<File> localFiles, String localFileUploadPath,
                                                String ftpFileDownloadPath) {
        for (File file : localFiles) {
            ftpFileDownloadPath = getPath(file.getPath(), localFileUploadPath, ftpFileDownloadPath);
            this.fileUploadToFtpAndDelLocalFile(file, ftpFileDownloadPath);
        }
    }

    /**
     * 文件上传到ftp;并且删除文件
     *
     * @param file
     */
    public void fileUploadToFtpAndDelLocalFile(File file, String ftpFileDownloadPath) {
        boolean base = fileUploadToFtp(file, ftpFileDownloadPath);
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
    private boolean fileUploadToFtp(File localFile, String ftpFileDownloadPath) {
        boolean base = false;
        try {
            if (FileUtil.isEmpty(localFile)) {
                return false;
            }
            base = ftpFileService.uploadFileFromProduction(ftpFileDownloadPath, localFile.getPath());
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

    private String getPath(String filePath, String localFileUploadPath, String ftpFileDownloadPath) {
        String path = StrUtil.subAfter(filePath, localFileUploadPath, true);
        return ftpFileDownloadPath + path;
    }

}
