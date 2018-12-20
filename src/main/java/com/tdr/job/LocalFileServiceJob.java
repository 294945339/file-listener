package com.tdr.job;

import cn.hutool.core.io.FileUtil;
import com.tdr.service.LocalFileService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author: dj
 * @Date: 2018/12/20 10:46
 * @Description:
 */

public class LocalFileServiceJob extends QuartzJobBean {

    @Value("${sys.localFilePath}")
    private String localFilePath;

    @Resource
    private LocalFileService localFileService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.go();
    }

    private void go() {
        //读取文件夹下面所有文件
        //遍历上传和删除
        File folder = FileUtil.file(localFilePath);
        //判断文件夹是否为空
        if (!FileUtil.isEmpty(folder)) {
            //不是空;则查询出文件下面所有的文件
            File[] files = FileUtil.ls(localFilePath);
            localFileService.fileUploadToFtpAndDelLocalFiles(files);
        }
    }
}
