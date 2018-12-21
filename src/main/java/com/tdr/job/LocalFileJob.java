package com.tdr.job;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.tdr.service.LocalFileService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @author: dj
 * @Date: 2018/12/20 10:46
 * @Description:
 */

@DisallowConcurrentExecution
public class LocalFileJob extends QuartzJobBean {

    private static final Log log = LogFactory.get();

    @Value("${sys.localFileUploadPath}")
    private String localFileUploadPath;

    @Value("${ftp.ftpFileDownloadPath}")
    private String ftpFileDownloadPath;

    @Resource
    private LocalFileService localFileService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("本地文件上传定时任务启动");
        this.upload();
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
