/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.quartz.utils;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.quartz.domain.QuartzJob;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Date;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * 定时任务管理
 * 分布式任务调度quartz中各种类名概念及为啥使用quartz而不使用timeTask？ https://www.cnblogs.com/chinaifae/articles/10375201.html
 * @author Zheng Jie
 * @date 2019-01-07
 */
@Slf4j
@Component
public class QuartzManage {

    private static final String JOB_NAME = "TASK_";
    // 配合 @Bean(name="scheduler") 结合使用，即byName自动装配
    @Resource(name = "scheduler")
    private Scheduler scheduler;

    public void addJob(QuartzJob quartzJob){
        try {
            // JobDetail https://www.cnblogs.com/qlqwjy/p/8721867.html
            // 构建job信息
            JobDetail jobDetail = JobBuilder.newJob(ExecutionJob.class).
                    // // 定义name,默认组是DEFAULT
                    withIdentity(JOB_NAME + quartzJob.getId()).build();

            //通过触发器名和cron 表达式创建 Trigger
            Trigger cronTrigger = newTrigger()
                    .withIdentity(JOB_NAME + quartzJob.getId())
                    .startNow()
                     // CronScheduleBuilder https://blog.csdn.net/fengqing5578/article/details/80352561
                    .withSchedule(CronScheduleBuilder.cronSchedule(quartzJob.getCronExpression()))
                    .build();
            // JobDataMap https://blog.csdn.net/qq_39529562/article/details/107415732
            // JobDataMap可以在定时任务中存储数据quartJob,可以通过触发器获取使用,但new出来的JobDataMap()则无此信息；通过自定义quartJob封装类可获得 用于获取其他xxxKey的信息
            cronTrigger.getJobDataMap().put(QuartzJob.JOB_KEY, quartzJob);

            //重置启动时间
            ((CronTriggerImpl)cronTrigger).setStartTime(new Date());

            //执行定时任务(通过调度器粘合工作和触发器)
            scheduler.scheduleJob(jobDetail,cronTrigger);

            // 暂停任务
            if (quartzJob.getIsPause()) {
                pauseJob(quartzJob);
            }
        } catch (Exception e){
            log.error("创建定时任务失败", e);
            throw new BadRequestException("创建定时任务失败");
        }
    }

    /**
     * 更新job cron表达式
     * @param quartzJob /
     */
    public void updateJobCron(QuartzJob quartzJob){
        try {
            // JobKey、TriggerKey、Trigger https://blog.csdn.net/qq_38846242/article/details/88696484
            // 获取指定上文配置 withIdentity 身份的一个对象，即找到指定id的trigger
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJob.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 如果不存在则创建一个定时任务
            if(trigger == null){
                // 通过该quartzJob创建并开启定时任务，由于上文通过triggerKey获取无效的trigger，故需重新创建获取
                addJob(quartzJob);
                trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            }
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzJob.getCronExpression());
            // 更新了Cron表达式，故需重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 重置启动时间
            ((CronTriggerImpl)trigger).setStartTime(new Date());
            // 更新QuartzJob.JOB_KEY对应的数据对象quartJob
            trigger.getJobDataMap().put(QuartzJob.JOB_KEY,quartzJob);
            // 不实现立刻执行的调度器 的操作 https://blog.csdn.net/u010904188/article/details/80915760
            // 执行重置调度器，会自动调用一遍Job(当前时间会立刻执行一次，随后就是只按照定时时间执行)
            scheduler.rescheduleJob(triggerKey, trigger);
            // 暂停任务
            if (quartzJob.getIsPause()) {
                pauseJob(quartzJob);
            }
        } catch (Exception e){
            log.error("更新定时任务失败", e);
            throw new BadRequestException("更新定时任务失败");
        }

    }

    /**
     * 删除一个job(不论有无trigger直接删)
     * @param quartzJob /
     */
    public void deleteJob(QuartzJob quartzJob){
        try {
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.pauseJob(jobKey);
            scheduler.deleteJob(jobKey);
        } catch (Exception e){
            log.error("删除定时任务失败", e);
            throw new BadRequestException("删除定时任务失败");
        }
    }

    /**
     * 恢复一个job
     * @param quartzJob /
     */
    public void resumeJob(QuartzJob quartzJob){
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJob.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 如果不存在则创建一个定时任务
            if(trigger == null) {
                addJob(quartzJob);
            }
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.resumeJob(jobKey);
        } catch (Exception e){
            log.error("恢复定时任务失败", e);
            throw new BadRequestException("恢复定时任务失败");
        }
    }

    /**
     * 立即执行job
     * @param quartzJob /
     */
    public void runJobNow(QuartzJob quartzJob){
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJob.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 如果不存在则创建一个定时任务
            if(trigger == null) {
                addJob(quartzJob);
            }
            // new JobDataMap()无quartzJob信息，但通过trigger获取的则有
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(QuartzJob.JOB_KEY, quartzJob);
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            // 立刻执行(当前的)触发已识别的JobDetail 任务
            scheduler.triggerJob(jobKey,dataMap);
        } catch (Exception e){
            log.error("定时任务执行失败", e);
            throw new BadRequestException("定时任务执行失败");
        }
    }

    /**
     * 暂停一个job
     * @param quartzJob /
     */
    public void pauseJob(QuartzJob quartzJob){
        try {
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.pauseJob(jobKey);
        } catch (Exception e){
            log.error("定时任务暂停失败", e);
            throw new BadRequestException("定时任务暂停失败");
        }
    }
}
