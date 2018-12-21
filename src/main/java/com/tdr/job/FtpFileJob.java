package com.tdr.job;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.tdr.service.FtpFileService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;

/**
 * @author: dj
 * @Date: 2018/12/21 15:26
 * @Description:
 */

@DisallowConcurrentExecution
public class FtpFileJob extends QuartzJobBean {

    private static final Log log = LogFactory.get();

    @Value("${sys.localFileDownloadPath}")
    private String localFileDownloadPath;

    @Value("${ftp.ftpFileUploadPath}")
    private String ftpFileUploadPath;

    @Resource
    private FtpFileService ftpFileService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("ftp文件下载定时任务启动");
        this.down();
    }

    private void down() {
        ftpFileService.downLoadDirectory(localFileDownloadPath, ftpFileUploadPath);
    }
}
