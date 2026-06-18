package com.payserver.module.admin.model.dto;

import lombok.Data;

/**
 * 排行数据项
 * 用于商品销量等排行榜展示
 */
@Data
public class RankItem {

    /** 名称 */
    private String name;
    /** 数值（如销量） */
    private java.math.BigDecimal value;
}
