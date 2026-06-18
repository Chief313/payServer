package com.payserver.module.admin.controller;

import com.payserver.common.result.Result;
import com.payserver.module.admin.model.dto.DashboardStatsVO;
import com.payserver.module.admin.service.DashboardService;
import com.payserver.security.RequireRole;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理后台 Dashboard 控制器
 * 提供首页统计数据接口
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取首页统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/stats")
    @RequireRole("ADMIN")
    public Result<DashboardStatsVO> stats() {
        return Result.ok(dashboardService.getStats());
    }
}
