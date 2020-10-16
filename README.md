---
title: Quartz定时任务调度的微服务
date: 2020-10-15
tags:
 - springboot
 - quartz
categories:
 -  Quartz
author: yzd
---
# springboot-quartz

#### 介绍
基于springboot+quartz+mybatis的定时任务调度微服务 可独立运行
通过框架Quartz实现定时任务的新增，删除，更新，暂停，恢复等操作

#### 创建定时任务
1. 数据库，详情请见项目中resources的sql文件
2. pom文件依赖
```maven
        <!--quartz依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>
```
#### 项目地址[Quartz](https://github.com/uuidFindMe/springboot-quartz-yzd.git)

