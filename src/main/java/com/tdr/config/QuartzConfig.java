package com.tdr.config;

import com.tdr.job.LocalFileServiceJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: dj
 * @Date: 2018/12/20 10:46
 * @Description:
 */

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail LocalFileServiceDetail() {
        return JobBuilder.newJob(LocalFileServiceJob.class).withIdentity("LocalFileService").storeDurably().build();
    }

    @Bean
    public Trigger LocalFileServiceTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("*/30 * * * * ?");
        return TriggerBuilder.newTrigger().forJob(LocalFileServiceDetail())
                .withIdentity("LocalFileService")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
