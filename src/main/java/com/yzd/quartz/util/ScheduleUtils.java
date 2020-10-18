
package com.yzd.quartz.util;


import com.yzd.quartz.Constant.ScheduleConstants;
import com.yzd.quartz.domain.SysJob;
import com.yzd.quartz.service.ISysJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.yzd.quartz.domain.SysJob.JOB_PARAM_KEY;

/**
 * description: 定时任务工具类
 *
 * @author yzd
 */
@Component
@Slf4j
public class ScheduleUtils {
	private final ISysJobService sysJobService;

	private final static String JOB_NAME = "TASK_";

	@Autowired
	public ScheduleUtils(ISysJobService sysJobService) {
		this.sysJobService = sysJobService;
	}

	/**
	 * 获取触发器key
	 */
	public static TriggerKey getTriggerKey(Long jobId) {
		return TriggerKey.triggerKey(JOB_NAME + jobId);
	}

	/**
	 * 获取触发器key
	 */
	public static TriggerKey getTriggerKey(SysJob sysJob) {
		return TriggerKey.triggerKey(JOB_NAME + sysJob.getJobId(), sysJob.getJobGroup());
	}

	/**
	 * 获取jobKey
	 */
	public static JobKey getJobKey(Long jobId) {
		return JobKey.jobKey(JOB_NAME + jobId);
	}

	/**
	 * 获取jobKey
	 */
	public static JobKey getJobKey(SysJob sysJob) {
		return JobKey.jobKey(JOB_NAME + sysJob.getJobId(), sysJob.getJobGroup());
	}

	/**
	 * 得到quartz任务类
	 *
	 * @param sysJob 执行计划
	 * @return 具体执行任务类
	 */
	private static Class<? extends Job> getQuartzJobClass(SysJob sysJob) {
		boolean isConcurrent = "0".equals(sysJob.getConcurrent());
		return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
	}

	/**
	 * 获取表达式触发器
	 */
	public static CronTrigger getCronTrigger(Scheduler scheduler, Long jobId) {
		try {
			return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
		} catch (SchedulerException e) {
			return null;
		}
	}

	/**
	 * 获取表达式触发器
	 */
	public static CronTrigger getCronTrigger(Scheduler scheduler, SysJob sysJob) {
		try {
			return (CronTrigger) scheduler.getTrigger(getTriggerKey(sysJob));
		} catch (SchedulerException e) {
			return null;
		}
	}

	/**
	 * 创建定时任务
	 */
	public static void createScheduleJob(Scheduler scheduler, SysJob sysJob) throws Exception {

		//构建job信息
		JobDetail jobDetail =
				JobBuilder.newJob(getQuartzJobClass(sysJob))
						.withIdentity(getJobKey(sysJob))
						.build();
		//表达式调度构建器
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(sysJob.getCronExpression());
		scheduleBuilder = handleCronScheduleMisfirePolicy(sysJob, scheduleBuilder);
		//按新的cronExpression表达式构建一个新的trigger
		CronTrigger trigger =
				TriggerBuilder.newTrigger().withIdentity(getTriggerKey(sysJob))
						.withSchedule(scheduleBuilder)
//						.withIdentity(sysJob.getJobName(),sysJob.getJobGroup())
						.withDescription(sysJob.getRemark())
						.build();
		//放入参数，运行时的方法可以获取
		jobDetail.getJobDataMap().put(JOB_PARAM_KEY, sysJob);
		scheduler.scheduleJob(jobDetail, trigger);
		//暂停任务
		if (ScheduleConstants.Status.PAUSE.getValue().equals(sysJob.getStatus())) {
			pauseJob(scheduler, sysJob);
		}
		//执行调度器
		scheduler.start();
	}

	/**
	 * 更新定时任务
	 */
	public static void updateScheduleJob(Scheduler scheduler, SysJob sysJob) throws Exception {
		TriggerKey triggerKey = getTriggerKey(sysJob);
		//表达式调度构建器
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(sysJob.getCronExpression());
		scheduleBuilder = handleCronScheduleMisfirePolicy(sysJob, scheduleBuilder);
		CronTrigger trigger = getCronTrigger(scheduler, sysJob);
		//按新的cronExpression表达式重新构建trigger
		trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
				.withSchedule(scheduleBuilder)
				.withDescription(sysJob.getRemark())
				.build();
		//参数
		trigger.getJobDataMap().put(JOB_PARAM_KEY, sysJob);
		scheduler.rescheduleJob(triggerKey, trigger);
		//暂停任务
		if (ScheduleConstants.Status.PAUSE.getValue().equals(sysJob.getStatus())) {
			pauseJob(scheduler, sysJob.getJobId());
		}
	}

	/**
	 * 立即执行任务
	 */
	public static void run(Scheduler scheduler, SysJob sysJob) throws Exception {
		//参数
		JobDataMap dataMap = new JobDataMap();
		dataMap.put(JOB_PARAM_KEY, sysJob);
		scheduler.triggerJob(getJobKey(sysJob), dataMap);
	}

	/**
	 * 暂停任务
	 */
	public static void pauseJob(Scheduler scheduler, Long jobId) throws Exception {
		scheduler.pauseJob(getJobKey(jobId));
	}

	/**
	 * 暂停任务
	 */
	public static void pauseJob(Scheduler scheduler, SysJob sysJob) throws Exception {
		scheduler.pauseJob(getJobKey(sysJob));
	}

	/**
	 * 恢复任务
	 */
	public static void resumeJob(Scheduler scheduler, Long jobId) throws Exception {
		scheduler.resumeJob(getJobKey(jobId));
	}

	/**
	 * 恢复任务
	 */
	public static void resumeJob(Scheduler scheduler, SysJob sysJob) throws Exception {
		scheduler.resumeJob(getJobKey(sysJob));
	}

	/**
	 * 删除定时任务
	 */
	public static void deleteScheduleJob(Scheduler scheduler, Long jobId) throws Exception {
		scheduler.deleteJob(getJobKey(jobId));
	}

	/**
	 * 删除定时任务
	 */
	public static void deleteScheduleJob(Scheduler scheduler, SysJob sysJob) throws Exception {
		scheduler.deleteJob(getJobKey(sysJob));
	}

	/**
	 * 设置定时任务策略
	 */
	public static CronScheduleBuilder handleCronScheduleMisfirePolicy(SysJob job, CronScheduleBuilder cb) throws Exception {
		switch (job.getMisfirePolicy()) {
			case ScheduleConstants.MISFIRE_DEFAULT:
				return cb;
			case ScheduleConstants.MISFIRE_IGNORE_MISFIRES:
				return cb.withMisfireHandlingInstructionIgnoreMisfires();
			case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED:
				return cb.withMisfireHandlingInstructionFireAndProceed();
			case ScheduleConstants.MISFIRE_DO_NOTHING:
				return cb.withMisfireHandlingInstructionDoNothing();
			default:
				return cb.withMisfireHandlingInstructionDoNothing();
		}
	}
}
