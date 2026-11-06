package com.power.service;

import com.power.dto.OrdersCancelDTO;
import com.power.dto.OrdersConfirmDTO;
import com.power.dto.OrdersPageQueryDTO;
import com.power.dto.OrdersPaymentDTO;
import com.power.dto.OrdersRejectionDTO;
import com.power.dto.OrdersSubmitDTO;
import com.power.result.PageResult;
import com.power.vo.OrderPaymentVO;
import com.power.vo.OrderStatisticsVO;
import com.power.vo.OrderSubmitVO;
import com.power.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询历史订单
     *
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult<OrderVO> pageQueryByUser(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    OrderVO details(Long id);

    /**
     * 查询当前采购用户的订单详情。
     *
     * @param id 订单ID
     * @return 订单详情
     */
    OrderVO detailsForUser(Long id);

    /**
     * 用户取消订单
     *
     * @param id
     */
    void userCancelById(Long id) throws Exception;

    /**
     * 采购用户确认收货。
     *
     * @param id 订单ID
     */
    void userComplete(Long id);

    /**
     * 再来一单
     *
     * @param id
     */
    void repetition(Long id);

    /**
     * 条件搜索订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult<OrderVO> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 确认订单
     *
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒绝订单
     *
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 运营人员取消订单
     *
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * 发货订单
     *
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);

    /**
     * 用户催办
     *
     * @param id
     */
    void reminder(Long id);

}
