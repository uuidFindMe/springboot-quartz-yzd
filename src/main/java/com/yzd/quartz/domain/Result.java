package com.yzd.quartz.domain;

import lombok.Data;

@Data
public class Result {
	/**
	 * 返回代码 0：处理成功 -1：处理失败
	 */
	private String resultCode;
	/**
	 * 返回说明
	 */
	private String resultMsg;
	/**
	 * 数据实体
	 */
	private Object resultData = null;

	public Result(String resultCode, String resultMsg) {
		this.resultCode = resultCode;
		this.resultMsg = resultMsg;
	}

	public Result(Object resultData) {
		this.resultCode = "0";
		this.resultMsg = "success";
		this.resultData = resultData;
	}

	public Result() {
	}

	public Result(String code) {
		this.resultCode = code;
	}

	public static Result success() {
		return new Result("0", "success");
	}

	public static Result success(Object obj) {
		return new Result(obj);
	}

	public static Result fail(String message) {
		return new Result("-1", message);
	}

	public static Result fail() {
		return new Result("-1", "fail");
	}

	public static Result returnResult(int rows) {
		return rows > 0 ? Result.success() : Result.fail();
	}
}
