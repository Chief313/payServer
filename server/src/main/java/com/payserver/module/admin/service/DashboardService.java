package com.payserver.module.admin.service;

import com.payserver.module.admin.model.dto.DashboardStatsVO;

/**
 * 管理后台统计业务接口
 */
public interface DashboardService {

    /**
     * 获取首页统计数据
     *
     * @return 统计数据
     */
    DashboardStatsVO getStats();
}
