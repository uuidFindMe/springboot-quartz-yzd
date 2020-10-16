package com.yzd.quartz.Constant;

/**
 * description:
 *
 * @author yzd
 */
public class ScheduleConstants {
	public static final String TASK_CLASS_NAME = "TASK_CLASS_NAME";

	/**
	 * 执行目标key
	 */
	public static final String TASK_PROPERTIES = "TASK_PROPERTIES";

	/**
	 * 默认
	 */
	public static final String MISFIRE_DEFAULT = "0";

	/**
	 * 立即触发执行
	 */
	public static final String MISFIRE_IGNORE_MISFIRES = "1";

	/**
	 * 触发一次执行
	 */
	public static final String MISFIRE_FIRE_AND_PROCEED = "2";

	/**
	 * 不触发立即执行
	 */
	public static final String MISFIRE_DO_NOTHING = "3";
	/**
	 * 当前页码
	 */
	public static final String PAGE = "page";
	/**
	 * 每页显示记录数
	 */
	public static final String LIMIT = "limit";
	/**
	 * 排序方式
	 */
	public static final String ORDER = "order";
	/**
	 * 升序
	 */
	public static final String ASC = "ASC";
	/**
	 * 升序
	 */
	public static final String DESC = "DESC";

	/**
	 * 定时任务状态
	 */
	public enum Status {
		/**
		 * 正常
		 */
		NORMAL("0"),
		/**
		 * 暂停
		 */
		PAUSE("1");

		private String value;

		private Status(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

}