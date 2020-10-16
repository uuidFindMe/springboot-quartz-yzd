package com.yzd.quartz.job;

import org.springframework.stereotype.Component;

/**
 * @author yzd
 * @projectName springboot-quartz
 * @packageName com.yzd.task.quartz
 * @company Peter
 * @date 2020/7/27  11:45
 * @description
 */
@Component("testTask2")
public class TaskTest2 implements BaseTask {
	@Override
	public void run(Object params) {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("-----------TaskTest2定时任务正在执行" + params.toString() + "," + "当前类=TaskTest2.run()");
	}
}
