package com.yzd.quartz.domain;

import com.yzd.quartz.Constant.ScheduleConstants;
import lombok.*;

import java.io.Serializable;

/**
 * 定时任务调度表 sys_job
 *
 * @author yzd
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysJob extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 任务调度参数key
	 */
	public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";
	/**
	 * spring bean名称 调用目标job的beanName
	 */
	private String beanName;
	/**
	 * spring bean名称 调用目标job的方法
	 */
	private String targetMethod;
	/**
	 * 任务ID
	 */
	private Long jobId;

	/**
	 * 任务名称
	 */
	private String jobName = "DEFAULT_JOB_NAME";

	/**
	 * 任务组名
	 */
	private String jobGroup = "DEFAULT_JOB_GROUP";

	/**
	 * cron执行表达式
	 */
	private String cronExpression;

	/**
	 * cron计划策略 0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
	 */
	private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

	/**
	 * 是否并发执行（0允许 1禁止）
	 */
	private String concurrent;

	/**
	 * 任务状态（0正常 1暂停）
	 */
	private String status;
}