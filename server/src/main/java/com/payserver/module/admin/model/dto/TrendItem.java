package com.payserver.module.admin.model.dto;

import lombok.Data;

/**
 * 趋势数据项
 * 用于图表展示日期与对应数值
 */
@Data
public class TrendItem {

    /** 日期，格式 yyyy-MM-dd */
    private String date;
    /** 数值 */
    private java.math.BigDecimal value;
}
