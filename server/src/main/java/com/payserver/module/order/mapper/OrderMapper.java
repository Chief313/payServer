package com.payserver.module.order.mapper;

import com.payserver.module.order.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据访问层
 */
@Mapper
public interface OrderMapper {

    /**
     * 插入订单
     *
     * @param order 订单实体
     * @return 影响行数
     */
    int insert(Order order);

    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单实体
     */
    Order findById(@Param("id") Long id);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单实体
     */
    Order findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 分页查询用户订单
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param size   每页大小
     * @return 订单列表
     */
    List<Order> listByUserId(@Param("userId") Long userId,
                             @Param("offset") int offset,
                             @Param("size") int size);

    /**
     * 统计用户订单数量
     *
     * @param userId 用户ID
     * @return 订单总数
     */
    long countByUserId(@Param("userId") Long userId);

    /**
     * 管理端分页查询订单
     *
     * @param status 订单状态，可为空
     * @param offset 偏移量
     * @param size   每页大小
     * @return 订单列表
     */
    List<Order> adminList(@Param("status") String status,
                          @Param("offset") int offset,
                          @Param("size") int size);

    /**
     * 管理端统计订单数量
     *
     * @param status 订单状态，可为空
     * @return 订单总数
     */
    long adminCount(@Param("status") String status);

    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 新状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新订单为已支付
     *
     * @param orderNo   订单号
     * @param payAmount 实付金额
     * @param payTime   支付时间
     * @return 影响行数
     */
    int updatePaid(@Param("orderNo") String orderNo,
                   @Param("payAmount") java.math.BigDecimal payAmount,
                   @Param("payTime") LocalDateTime payTime);

    /**
     * 更新订单为已发货
     *
     * @param id       订单ID
     * @param shipTime 发货时间
     * @return 影响行数
     */
    int updateShipped(@Param("id") Long id, @Param("shipTime") LocalDateTime shipTime);

    /**
     * 查询超时未支付订单
     *
     * @param status  订单状态
     * @param deadline 截止时间（早于此时间的待支付订单将被取消）
     * @return 超时订单列表
     */
    List<Order> listTimeoutOrders(@Param("status") String status,
                                  @Param("deadline") LocalDateTime deadline);
}
