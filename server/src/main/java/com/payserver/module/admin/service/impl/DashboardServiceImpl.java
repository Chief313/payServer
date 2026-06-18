package com.payserver.module.admin.service.impl;

import com.payserver.module.admin.mapper.DashboardMapper;
import com.payserver.module.admin.model.dto.DashboardStatsVO;
import com.payserver.module.admin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 管理后台统计业务实现类
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;

    /**
     * 聚合首页各项统计数据
     */
    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO vo = new DashboardStatsVO();
        vo.setTotalUsers(dashboardMapper.countUsers());
        vo.setTotalOrders(dashboardMapper.countOrders());
        BigDecimal totalSales = dashboardMapper.sumSales();
        vo.setTotalSales(totalSales != null ? totalSales : BigDecimal.ZERO);
        vo.setTodayOrders(dashboardMapper.countTodayOrders());
        vo.setOrderTrend(dashboardMapper.orderTrendLast7Days());
        vo.setSalesTrend(dashboardMapper.salesTrendLast7Days());
        vo.setTopProducts(dashboardMapper.topProductsTop5());
        return vo;
    }
}
