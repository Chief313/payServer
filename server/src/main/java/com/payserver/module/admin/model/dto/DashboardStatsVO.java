package com.payserver.module.admin.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 管理后台首页统计数据
 * 供 Dashboard 图表与统计卡片使用
 */
@Data
public class DashboardStatsVO {

    /** 用户总数 */
    private long totalUsers;
    /** 订单总数 */
    private long totalOrders;
    /** 销售总额 */
    private BigDecimal totalSales;
    /** 今日订单数 */
    private long todayOrders;
    /** 近7日订单趋势 */
    private List<TrendItem> orderTrend;
    /** 近7日销售额趋势 */
    private List<TrendItem> salesTrend;
    /** 商品销量 Top5 */
    private List<RankItem> topProducts;
}
