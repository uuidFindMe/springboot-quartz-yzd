package com.yzd.quartz.controller;

import com.github.pagehelper.PageHelper;
import com.yzd.quartz.domain.Result;
import com.yzd.quartz.domain.SysJob;
import com.yzd.quartz.service.ISysJobService;
import com.yzd.quartz.util.CronUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 调度任务信息操作处理
 *
 * @author yzd
 */
@Api(description = "调度任务操作接口")
@RestController
@RequestMapping("/job")
public class SysJobController {

    @Resource
    private ISysJobService jobService;

    /**
     * 查询定时任务列表
     */
    @ApiOperation(value = "查询定时任务列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result list(@RequestBody SysJob sysJob) {
        Assert.notNull(sysJob.getPageNum(), "pageNum is null");
        Assert.notNull(sysJob.getPageSize(), "pageSize is null");
        Result result = Result.success();
        PageHelper.startPage(sysJob.getPageNum(),sysJob.getPageSize());
        result.setResultData(jobService.selectJobList(sysJob));
        return result;
    }


    /**
     * 获取定时任务详细信息
     */
    @ApiOperation(value = "获取定时任务详细信息")
    @RequestMapping(value = "/{jobId}", method = RequestMethod.POST)
//	@RequestMapping(value = "/getInfo",method = RequestMethod.POST )
    public Result getInfo(@RequestBody @PathVariable("jobId") Long jobId) {
        Assert.notNull(jobId, "jobId is null");
        return Result.success(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @ApiOperation(value = "新增定时任务")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody SysJob sysJob) throws Exception {
        Assert.notNull(sysJob.getBeanName(), "目标类 beanName is null");
        Assert.notNull(sysJob.getTargetMethod(), "目标方法 targetMethod is null");
        Assert.notNull(sysJob.getCronExpression(), "CronExpression is null");
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
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Result edit(@RequestBody SysJob sysJob) throws Exception {
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
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    public Result changeStatus(@RequestBody SysJob job) throws Exception {
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
    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public Result run(@RequestBody SysJob job) throws Exception {
        Assert.notNull(job.getJobId(), "jobId is null");
        Assert.notNull(job.getJobGroup(), "jobGroup is null");
        jobService.run(job);
        return Result.success();
    }

    /**
     * 删除定时任务
     */
    @ApiOperation(value = "删除定时任务")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Result remove(@RequestBody Long[] jobIds) throws Exception {
        jobService.deleteJobByIds(jobIds);
        return Result.success();
    }
}
