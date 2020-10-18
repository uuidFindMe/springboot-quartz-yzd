package com.yzd.quartz.service.impl;

import com.yzd.quartz.dao.SysJobMapper;
import com.yzd.quartz.Constant.ScheduleConstants;
import com.yzd.quartz.domain.SysJob;
import com.yzd.quartz.service.ISysJobService;
import com.yzd.quartz.util.CronUtils;
import com.yzd.quartz.util.ScheduleUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * 定时任务调度信息 服务层
 *
 * @author yzd
 */
@Service
@Slf4j
public class SysJobServiceImpl implements ISysJobService {
	@Resource(name = "Scheduler")
	private Scheduler scheduler;

	private final SysJobMapper jobMapper;

	@Autowired
	public SysJobServiceImpl(SysJobMapper jobMapper) {
		this.jobMapper = jobMapper;
	}

	/**
	 * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
	 */
//	@PostConstruct
//	public void init() throws Exception {
//		//清除所有任务：重新添加
//		scheduler.clear();
//		List<SysJob> jobList = jobMapper.selectJobAll();
//		log.info("项目启动时，初始化定时器============");
//		for (SysJob job : jobList) {
////			CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, job.getJobId());
////			//如果不存在，则创建
////			if (cronTrigger == null) {
////				ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
////			} else {
////				ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
////			}
//			ScheduleUtils.createScheduleJob(scheduler, job);
//		}
//	}

	/**
	 * 获取quartz调度器的计划任务列表
	 *
	 * @param job 调度信息
	 */
	@Override
	public List<SysJob> selectJobList(SysJob job) {
		return jobMapper.selectJobList(job);
	}

	/**
	 * 通过调度任务ID查询调度信息
	 *
	 * @param jobId 调度任务ID
	 * @return 调度任务对象信息
	 */
	@Override
	public SysJob selectJobById(Long jobId) {
		return jobMapper.selectJobById(jobId);
	}

	/**
	 * 暂停任务
	 *
	 * @param job 调度信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int pauseJob(SysJob job) throws SchedulerException {
		job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
		int rows = jobMapper.updateJob(job);
		if (rows > 0) {
			scheduler.pauseJob(ScheduleUtils.getJobKey(job));
		}
		return rows;
	}

	/**
	 * 恢复任务
	 *
	 * @param job 调度信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int resumeJob(SysJob job) throws Exception {
		job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
		int rows = jobMapper.updateJob(job);
		if (rows > 0) {
			scheduler.resumeJob(ScheduleUtils.getJobKey(job));
		}
		return rows;
	}

	/**
	 * 删除任务后，所对应的trigger也将被删除
	 *
	 * @param job 调度信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteJob(SysJob job) throws SchedulerException {
		int rows = jobMapper.deleteJobById(job.getJobId());
		if (rows > 0) {
			scheduler.deleteJob(ScheduleUtils.getJobKey(job));
		}
		return rows;
	}

	/**
	 * 批量删除调度信息
	 *
	 * @param jobIds 需要删除的任务ID
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteJobByIds(Long[] jobIds) throws Exception {
		for (Long jobId : jobIds) {
			SysJob job = jobMapper.selectJobById(jobId);
			if (job != null) {
				deleteJob(job);
			}
		}
	}

	/**
	 * 任务调度状态修改
	 *
	 * @param job 调度信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int changeStatus(SysJob job) throws Exception {
		int rows = 0;
		String status = job.getStatus();
		if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
			rows = resumeJob(job);
		} else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
			rows = pauseJob(job);
		}
		return rows;
	}

	/**
	 * 立即运行任务 只执行一次的任务
	 *
	 * @param job 调度信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void run(SysJob job) throws SchedulerException {
		Long jobId = job.getJobId();
		SysJob oldSysJob = selectJobById(jobId);
		// 参数
		JobDataMap dataMap = new JobDataMap();
		dataMap.put(ScheduleConstants.TASK_PROPERTIES, oldSysJob);
		scheduler.triggerJob(ScheduleUtils.getJobKey(job), dataMap);
	}

	/**
	 * 新增任务
	 *
	 * @param job 调度信息 调度信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertJob(SysJob job) throws Exception {
		job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
		int rows = jobMapper.insertJob(job);
		if (rows > 0) {
			ScheduleUtils.createScheduleJob(scheduler, job);
		}
		return rows;
	}

	/**
	 * 更新任务的时间表达式
	 *
	 * @param job 调度信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateJob(SysJob job) throws Exception {
		SysJob oldJob = selectJobById(job.getJobId());
		int rows = jobMapper.updateJob(job);
		if (rows > 0) {
			updateSchedulerJob(job, oldJob);
		}
		return rows;
	}

	/**
	 * 更新任务
	 *
	 * @param job 任务对象
	 */
	public void updateSchedulerJob(SysJob job, SysJob oldJob) throws Exception {
		// 判断是否存在
		JobKey jobKey = ScheduleUtils.getJobKey(oldJob);
		if (scheduler.checkExists(jobKey)) {
			// 防止创建时存在数据问题 先移除，然后在执行创建操作
			scheduler.deleteJob(jobKey);
		}
		ScheduleUtils.createScheduleJob(scheduler, job);
	}

	/**
	 * 校验cron表达式是否有效
	 *
	 * @param cronExpression 表达式
	 * @return 结果
	 */
	@Override
	public boolean checkCronExpressionIsValid(String cronExpression) {
		return CronUtils.isValid(cronExpression);
	}
}