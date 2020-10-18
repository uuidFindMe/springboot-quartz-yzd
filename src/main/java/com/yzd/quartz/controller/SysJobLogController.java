package com.yzd.quartz.controller;

import com.github.pagehelper.PageHelper;
import com.yzd.quartz.domain.Result;
import com.yzd.quartz.domain.SysJobLog;
import com.yzd.quartz.service.ISysJobLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度日志操作处理
 *
 * @author yzd
 */
@Api(description = "调度日志操作接口")
@RestController
@RequestMapping("/jobLog")
public class SysJobLogController {
    private final ISysJobLogService jobLogService;

    @Autowired
    public SysJobLogController(ISysJobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    /**
     * 查询定时任务调度日志列表
     */
    @ApiOperation(value = "查询定时任务调度日志列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result list(@RequestBody SysJobLog sysJobLog) {
        Assert.notNull(sysJobLog.getPageNum(), "pageNum is null");
        Assert.notNull(sysJobLog.getPageSize(), "pageSize is null");
        Result result = Result.success();
        PageHelper.startPage(sysJobLog.getPageSize(), sysJobLog.getPageNum());
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        result.setResultData(list);
        return result;
    }

    /**
     * 根据调度编号获取详细信息
     */
    @ApiOperation(value = "根据调度编号获取详细信息")
//	@RequestMapping(value = "/{jobLogId}")
    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public Result getInfo(@RequestBody @PathVariable Long jobLogId) {
        Assert.notNull(jobLogId, "jobLogId is null");
        return Result.success(jobLogService.selectJobLogById(jobLogId));
    }

    /**
     * 删除定时任务调度日志
     */
    @ApiOperation(value = "删除定时任务调度日志")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Result remove(@RequestBody @PathVariable Long[] jobLogIds) {
        return Result.returnResult(jobLogService.deleteJobLogByIds(jobLogIds));
    }

    /**
     * 清空定时任务调度日志
     */
    @ApiOperation(value = "清空定时任务调度日志")
    @RequestMapping(value = "/clean", method = RequestMethod.POST)
    public Result clean() {
        jobLogService.cleanJobLog();
        return Result.success();
    }
}
