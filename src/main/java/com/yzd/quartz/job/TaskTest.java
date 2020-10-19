package com.yzd.quartz.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yzd
 * @projectName springboot-quartz
 * @packageName com.yzd.task.quartz
 * @company Peter
 * @date 2020/7/27  11:45
 * @description
 */
@Component("testTask")
public class TaskTest {
	@Autowired
	private TaskTest2 taskTest2;

	public void run(Object params) {
		taskTest2.run(params);
		System.out.println("-----------TaskTest定时任务正在执行" + params + "," + "当前类=TaskTest.run()");
	}
}
