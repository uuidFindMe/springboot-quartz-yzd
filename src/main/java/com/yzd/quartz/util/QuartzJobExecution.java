

package com.yzd.quartz.util;


import com.yzd.quartz.domain.SysJob;
import com.yzd.quartz.domain.SysJobLog;
import com.yzd.quartz.service.ISysJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;


/**
 * description:定时任务执行类（并发执行）
 *
 * @author yzd
 */
@Component
@Slf4j
public class QuartzJobExecution extends QuartzJobBean {
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
			log.info("并发执行任务===任务准备执行，任务ID：" + sysJob.getJobId());
			//获取java类对象
			Object target = SpringContextUtils.getBean(sysJob.getBeanName());
			String methodStr = StringUtils.isBlank(sysJob.getTargetMethod()) ? "run" : sysJob.getTargetMethod();
			//获取执行方法
			Method method = target.getClass().getDeclaredMethod(methodStr, Object.class);
			method.setAccessible(true);
			//执行
			method.invoke(target, sysJob.getParams());
			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			log.info("并发执行任务===任务执行完毕，任务ID：" + sysJob.getJobId() + "  总共耗时：" + times + "毫秒");
			//写入log表
			after(context, sysJob, null);
		} catch (Exception e) {
			log.error("并发执行任务===任务执行失败，任务ID：" + sysJob.getJobId(), e);
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
		Date startTime = threadLocal.get();
		threadLocal.remove();
		SysJobLog sysJobLog = new SysJobLog();
		sysJobLog.setJobName(sysJob.getJobName());
		sysJobLog.setJobGroup(sysJob.getJobGroup());
		sysJobLog.setBeanName(sysJob.getBeanName());
		sysJobLog.setStartTime(startTime);
		sysJobLog.setStopTime(new Date());
		long runMs = sysJobLog.getStopTime().getTime() - sysJobLog.getStartTime().getTime();
		sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
		if (e != null) {
			sysJobLog.setStatus("1");
			String errorMsg = StringUtils.substring(e.getMessage(), 0, 2000);
			sysJobLog.setExceptionInfo(errorMsg);
		} else {
			sysJobLog.setStatus("0");
		}
		// 写入数据库当中
		SpringContextUtils.getBean(ISysJobLogService.class).addJobLog(sysJobLog);
	}

}
