package com.payserver.module.admin.mapper;

import com.payserver.module.admin.model.dto.RankItem;
import com.payserver.module.admin.model.dto.TrendItem;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * 管理后台统计数据访问层
 */
@Mapper
public interface DashboardMapper {

    /**
     * 统计用户总数
     *
     * @return 用户数量
     */
    long countUsers();

    /**
     * 统计订单总数
     *
     * @return 订单数量
     */
    long countOrders();

    /**
     * 统计销售总额（已支付及后续状态订单的实付金额之和）
     *
     * @return 销售总额
     */
    BigDecimal sumSales();

    /**
     * 统计今日订单数
     *
     * @return 今日订单数量
     */
    long countTodayOrders();

    /**
     * 查询近7日订单趋势
     *
     * @return 趋势数据列表
     */
    List<TrendItem> orderTrendLast7Days();

    /**
     * 查询近7日销售额趋势
     *
     * @return 趋势数据列表
     */
    List<TrendItem> salesTrendLast7Days();

    /**
     * 查询商品销量 Top5
     *
     * @return 排行数据列表
     */
    List<RankItem> topProductsTop5();
}
