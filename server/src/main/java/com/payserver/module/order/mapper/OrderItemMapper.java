package com.payserver.module.order.mapper;

import com.payserver.module.order.model.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单明细数据访问层
 */
@Mapper
public interface OrderItemMapper {

    /**
     * 批量插入订单明细
     *
     * @param items 明细列表
     * @return 影响行数
     */
    int batchInsert(@Param("items") List<OrderItem> items);

    /**
     * 根据订单ID查询明细
     *
     * @param orderId 订单ID
     * @return 明细列表
     */
    List<OrderItem> listByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单号关联查询明细（通过 t_order 关联）
     *
     * @param orderNo 订单号
     * @return 明细列表
     */
    List<OrderItem> listByOrderNo(@Param("orderNo") String orderNo);
}
