package com.yzd.quartz.job;

/**
 * Created by  yzd
 * Date:2020-10-16.
 * Time:15:50
 */
public interface BaseTask {
	void run(Object params) throws Exception;
}
