package com.tdr.job;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.tdr.service.FtpFileService;
import com.tdr.service.LocalFileService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * @author: dj
 * @Date: 2018/12/20 10:46
 * @Description:
 */

public class LocalFileServiceJob extends QuartzJobBean {

    @Value("${sys.localFileUploadPath}")
    private String localFileUploadPath;

    @Value("${sys.localFileDownloadPath}")
    private String localFileDownloadPath;

    @Value("${ftp.ftpFileUploadPath}")
    private String ftpFileUploadPath;

    @Value("${ftp.ftpFileDownloadPath}")
    private String ftpFileDownloadPath;

    @Resource
    private LocalFileService localFileService;

    @Resource
    private FtpFileService ftpFileService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.upload();
        this.down();
    }

    private void down() {
        ftpFileService.downLoadDirectory(localFileDownloadPath, ftpFileUploadPath);
    }

    /**
     * 上传本地文件
     */
    private void upload() {
        File[] files = FileUtil.ls(localFileUploadPath);
        for (File f : files) {
            List<File> fileList = FileUtil.loopFiles(f.getPath());
            localFileService.fileUploadToFtpAndDelLocalFiles(fileList, ftpFileDownloadPath);
        }
        localFileService.clear(new File(localFileUploadPath));
    }

}
