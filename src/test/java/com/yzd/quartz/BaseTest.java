package com.yzd.quartz;


import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @title: BaseTest
 * @description: 单元测试 基类
 * @author yzd
 * @date 2019/12/5 22:26
 */
@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {TestConfiguration.class})
@SpringBootTest(classes=QuartzApplication.class)
public class BaseTest {
}
