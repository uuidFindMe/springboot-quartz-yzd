package com.yzd.quartz.controller;

import com.github.pagehelper.PageHelper;
import com.yzd.quartz.domain.Result;
import com.yzd.quartz.domain.SysJob;
import com.yzd.quartz.service.ISysJobService;
import com.yzd.quartz.util.CronUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * 调度任务信息操作处理
 *
 * @author yzd
 */
@Api(description = "调度任务操作接口")
@RestController
@RequestMapping("/job")
public class SysJobController {

	@Autowired
	private ISysJobService jobService;

	/**
	 * 查询定时任务列表
	 */
	@ApiOperation(value = "查询定时任务列表")
	@RequestMapping("/list")
	public Result list(SysJob sysJob) {
		Assert.notNull(sysJob.getPageNum(), "pageNum is null");
		Assert.notNull(sysJob.getPageSize(), "pageSize is null");
		Result result = Result.success();
		PageHelper.startPage(sysJob.getPageSize(), sysJob.getPageNum());
		result.setResultData(jobService.selectJobList(sysJob));
		return result;
	}


	/**
	 * 获取定时任务详细信息
	 */
	@ApiOperation(value = "获取定时任务详细信息")
	@RequestMapping(value = "/{jobId}")
	public Result getInfo(@PathVariable("jobId") Long jobId) {
		Assert.notNull(jobId, "jobId is null");
		return Result.success(jobService.selectJobById(jobId));
	}

	/**
	 * 新增定时任务
	 */
	@ApiOperation(value = "新增定时任务")
	@RequestMapping(value = "/add")
	public Result add(SysJob sysJob) throws Exception {
		if (!CronUtils.isValid(sysJob.getCronExpression())) {
			return Result.fail("cron表达式不正确");
		}
		sysJob.setCreateBy("admin");
		return Result.returnResult(jobService.insertJob(sysJob));
	}

	/**
	 * 修改定时任务
	 */
	@ApiOperation(value = "修改定时任务")
	@RequestMapping(value = "/edit")
	public Result edit(SysJob sysJob) throws Exception {
		Assert.notNull(sysJob.getJobId(), "jobId is null");
		if (!CronUtils.isValid(sysJob.getCronExpression())) {
			return Result.fail("cron表达式不正确");
		}
		sysJob.setCreateBy("admin");
		return Result.returnResult(jobService.updateJob(sysJob));
	}

	/**
	 * 定时任务状态修改 暂停/恢复 （0正常 1暂停）
	 */
	@ApiOperation(value = "定时任务状态修改 暂停/恢复 （0正常 1暂停）")
	@RequestMapping("/changeStatus")
	public Result changeStatus(SysJob job) throws Exception {
		Assert.notNull(job.getJobId(), "jobId is null");
		Assert.notNull(job.getStatus(), "status is null");
		SysJob newJob = jobService.selectJobById(job.getJobId());
		newJob.setStatus(job.getStatus());
		return Result.returnResult(jobService.changeStatus(newJob));
	}

	/**
	 * 定时任务立即执行一次 只执行一次的任务
	 */
	@ApiOperation(value = "定时任务立即执行一次 只执行一次的任务")
	@RequestMapping("/run")
	public Result run(SysJob job) throws Exception {
		Assert.notNull(job.getJobId(), "jobId is null");
		Assert.notNull(job.getJobGroup(), "jobGroup is null");
		jobService.run(job);
		return Result.success();
	}

	/**
	 * 删除定时任务
	 */
	@ApiOperation(value = "删除定时任务")
	@RequestMapping("/remove")
	public Result remove(Long[] jobIds) throws Exception {
		jobService.deleteJobByIds(jobIds);
		return Result.success();
	}
}
