package com.tdr.config;

import com.tdr.job.LocalFileServiceJob;
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
    public JobDetail localFileServiceDetail() {
        return JobBuilder.newJob(LocalFileServiceJob.class).withIdentity("LocalFileService").storeDurably().build();
    }

    @Bean
    public Trigger localFileServiceTrigger() {
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(jobInterval).repeatForever();
        return TriggerBuilder.newTrigger().forJob(localFileServiceDetail())
                .withIdentity("LocalFileService")
                .withSchedule(simpleScheduleBuilder)
                .build();
    }
}
