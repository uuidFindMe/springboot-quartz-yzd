package com.yzd.quartz.util;

import com.yzd.quartz.domain.SysJob;
import com.yzd.quartz.domain.SysJobLog;
import com.yzd.quartz.service.ISysJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 定时任务处理（禁止并发执行  等上一个任务结束才继续下一个任务）
 *
 * @author yzd
 */
@DisallowConcurrentExecution
@Component
@Slf4j
public class QuartzDisallowConcurrentExecution extends QuartzJobBean {
	/**
	 * 线程本地变量
	 */
	private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		//设置开始时间
		threadLocal.set(new Date());
		SysJob sysJob = (SysJob) context.getMergedJobDataMap()
				.get(SysJob.JOB_PARAM_KEY);

		//任务开始时间
		long startTime = System.currentTimeMillis();
		try {
			//执行任务
			log.info("非并发执行任务===准备执行，任务ID：" + sysJob.getJobId());
			//获取java类对象
			Object target = SpringContextUtils.getBean(sysJob.getBeanName());
			//获取执行方法
			Method method = target.getClass().getDeclaredMethod("run", Object.class);
			method.setAccessible(true);
			//执行
			method.invoke(target, sysJob);
			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			log.info("非并发执行任务===执行完毕，任务ID：" + sysJob.getJobId() + "  总共耗时：" + times + "毫秒");
			//写入log表
			after(context, sysJob, null);
		} catch (Exception e) {
			log.error("非并发执行任务===任务执行失败，任务ID：" + sysJob.getJobId(), e);
			after(context, sysJob, e);
		}
	}
	/**
	 * 执行后
	 *
	 * @param context 工作执行上下文对象
	 * @param sysJob  系统计划任务
	 */
	private void after(JobExecutionContext context, SysJob sysJob, Exception e) {
		Date start = threadLocal.get();
		threadLocal.remove();
		SysJobLog sysJobLog = new SysJobLog();
		sysJobLog.setStartTime(start);
		sysJobLog.setJobName(sysJob.getJobName());
		sysJobLog.setJobGroup(sysJob.getJobGroup());
		sysJobLog.setBeanName(sysJob.getBeanName());
		sysJobLog.setStopTime(new Date());
		long runMs = sysJobLog.getStopTime().getTime() - sysJobLog.getStartTime().getTime();
		sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
		if (e != null) {
			sysJobLog.setStatus("1");
			sysJobLog.setExceptionInfo(StringUtils.substring(e.getCause().toString(), 0, 2000));
		} else {
			sysJobLog.setStatus("0");
		}
		// 写入数据库当中
		SpringContextUtils.getBean(ISysJobLogService.class).addJobLog(sysJobLog);
	}
}
