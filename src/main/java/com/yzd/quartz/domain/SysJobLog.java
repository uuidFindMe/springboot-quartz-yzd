package com.yzd.quartz.domain;

import lombok.*;

import java.util.Date;

/**
 * 定时任务调度日志表 sys_job_log
 *
 * @author yzd
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysJobLog extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	private Long jobLogId;

	/**
	 * 任务名称
	 */
	private String jobName;

	/**
	 * 任务组名
	 */
	private String jobGroup;

	/**
	 * 调用目标job的beanName
	 */
	private String beanName;

	/**
	 * 日志信息
	 */
	private String jobMessage;

	/**
	 * 执行状态（0正常 1失败）
	 */
	private String status;

	/**
	 * 异常信息
	 */
	private String exceptionInfo;

	/**
	 * 开始时间
	 */
	private Date startTime;

	/**
	 * 停止时间
	 */
	private Date stopTime;
}
