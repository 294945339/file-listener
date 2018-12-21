package com.tdr.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.tdr.util.PathUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Objects;

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
    public void fileUploadToFtpAndDelLocalFiles(List<File> localFiles, String ftpFileDownloadPath) {
        for (File file : localFiles) {
            String ftpFilePath = PathUtil.getFtpUploadPath(file.getPath(), ftpFileDownloadPath);
            this.fileUploadToFtpAndDelLocalFile(file, ftpFilePath);
        }
    }

    /**
     * 文件上传到ftp;并且删除文件
     *
     * @param file
     * @param ftpFileDownloadPath
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
     * @param ftpFileDownloadPath
     * @return
     */
    private boolean fileUploadToFtp(File localFile, String ftpFileDownloadPath) {
        boolean base = false;
        try {
            base = ftpFileService.uploadFileFromProduction(localFile.getPath(), ftpFileDownloadPath);
//            base = ftpFileService.uploadDirectory(localFilePath, ftpFileDownloadPath);
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

    /**
     * 清理本地空文件夹
     *
     * @param f
     */
    public void clear(File f) {
        for (File f1 : Objects.requireNonNull(f.listFiles())) {
            if (f1.isDirectory()) {
                clear(f1);
                //一直递归到最后的目录
                if (Objects.requireNonNull(f1.listFiles()).length == 0) {
                    //如果是文件夹里面没有文件证明是空文件，进行删除
                    FileUtil.del(f1);
                }
            }
        }
    }

}
