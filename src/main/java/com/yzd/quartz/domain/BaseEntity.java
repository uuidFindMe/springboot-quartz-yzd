package com.yzd.quartz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity基类
 * @author yzd
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 当前页码
	 */
	private Integer pageNum;
	/**
	 * 每页显示条数
	 */
	private Integer pageSize;
	/**
	 * 搜索值
	 */
	private String searchValue;

	/**
	 * 创建者
	 */
	private String createBy;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 更新者
	 */
	private String updateBy;

	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 开始时间
	 */
	@JsonIgnore
	private String beginTime;

	/**
	 * 结束时间
	 */
	@JsonIgnore
	private String endTime;

	/**
	 * 请求参数
	 */
	private Object params;
}
