package com.tdr.config;

import com.tdr.job.FtpFileJob;
import com.tdr.job.LocalFileJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: dj
 * @Date: 2018/12/20 10:46
 * @Description:
 */

@Configuration
public class QuartzConfig {

    @Value("${sys.jobInterval}")
    private int jobInterval;

    @Bean
    public JobDetail localFileDetail() {
        return JobBuilder.newJob(LocalFileJob.class).withIdentity("LocalFileJob").storeDurably().build();
    }

    @Bean
    public JobDetail ftpFileDetail() {
        return JobBuilder.newJob(FtpFileJob.class).withIdentity("FtpFileJob").storeDurably().build();
    }

    @Bean
    public Trigger localFileServiceTrigger() {
        return TriggerBuilder.newTrigger().forJob(localFileDetail())
                .withIdentity("LocalFileJob")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(jobInterval).repeatForever())
                .build();
    }

    @Bean
    public Trigger ftpFileServiceTrigger() {
        return TriggerBuilder.newTrigger().forJob(ftpFileDetail())
                .withIdentity("FtpFileJob")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(jobInterval).repeatForever())
                .build();
    }
}
