package com.yzd.quartz.config;

import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

/**
 * Title: SchedulerConfig
 * ProjectName springboot-quartz
 * Description: quartz.properties文件进行读取
 * @Author: yzd
 * Date: 2020-10-13  14:06
 */
@Configuration
public class SchedulerConfig {

	private final MyJobFactory myJobFactory;

	@Autowired
	public SchedulerConfig(MyJobFactory myJobFactory) {
		this.myJobFactory = myJobFactory;
	}

	@Bean(name="SchedulerFactory")
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
		//Spring提供SchedulerFactoryBean为Scheduler提供配置信息,并被Spring容器管理其生命周期
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		//启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
		factory.setOverwriteExistingJobs(true);
		// 延时启动(秒)
		factory.setStartupDelay(20);
		//设置quartz的配置文件
		factory.setQuartzProperties(quartzProperties());
		//设置自定义Job Factory，用于Spring管理Job bean
		factory.setJobFactory(myJobFactory);
		return factory;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		// 配置文件路径
		// 注：配置文件名不能与默认的文件quartz.properties相同，否则报：Active Scheduler of name 'MyClusterScheduler' already registered in Quartz SchedulerRepository. Cannot create a new Spring-managed Scheduler of the same name!
		propertiesFactoryBean.setLocation(new ClassPathResource("/myQuartz.properties"));
		//在quartz.properties中的属性被读取并注入后再初始化对象
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	/**
	 * quartz初始化监听器
	 * 这个监听器可以监听到工程的启动，在工程停止再启动时可以让已有的定时任务继续进行。
	 */
	@Bean
	public QuartzInitializerListener executorListener() {
		return new QuartzInitializerListener();
	}

	/**
	 *
	 *通过SchedulerFactoryBean获取Scheduler的实例
	 */

	@Bean(name="Scheduler")
	public Scheduler scheduler() throws IOException {
		return schedulerFactoryBean().getScheduler();
	}

}


