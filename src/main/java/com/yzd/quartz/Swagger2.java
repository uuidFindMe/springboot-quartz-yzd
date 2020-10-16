package com.yzd.quartz;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
/** 是否开启swagger，正式环境一般是需要关闭的（避免不必要的漏洞暴露！），可根据springboot的多环境配置进行设置 */
@ConditionalOnProperty(name = "swagger.enable", havingValue = "true")
public class Swagger2 {

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("demo")
				.genericModelSubstitutes(DeferredResult.class)
				.useDefaultResponseMessages(false)
				.apiInfo(apiInfo())
				.pathMapping("/")
				.select()
				//a.b.c配置多个
				.apis(RequestHandlerSelectors.basePackage("com.yzd.quartz.controller"))
				//只扫描有api注解的类
				.apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
				//只扫描有ApiOperation注解的方法
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				.paths(PathSelectors.any())
				// .paths(PathSelectors.none())//如果是线上环境，添加路径过滤，设置为全部都不符合
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("springboot-quartz项目 RESTful APIs")
				.description("springboot-quartz项目后台api接口文档")
				.termsOfServiceUrl("quartz.yzd.com")
				.version("10.0")
				.build();
	}

}
