package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderSubmitVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 用户下单
     * @param orders
     * @return
     */
    /*@Insert("insert into orders(number, status, user_id, address_book_id, order_time, checkout_time, pay_method, pay_status, amount, remark, phone, address, user_name, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_status, delivery_time, pack_amount, tableware_number, tableware_status)" +
            "values (#{number},#{status},#{user_id},#{address_book_id},#{order_time},#{checkout_time},#{pay_method},#{pay_status},#{amount},#{remark},#{phone},#{address},#{user_name},#{consignee},#{cancel_reason},#{rejection_reason},#{cancel_time},#{estimated_delivery_time},#{delivery_status},#{delivery_time},#{pack_amount},#{tableware_number},#{tableware_status})")
    */
    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);



    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);
    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 自动取消订单和完成派送
     * @param time
     * @param pendingPayment
     * @return
     */
    @Select("select * from orders where order_time <#{time} && status = #{pendingPayment}")
    List<Orders> getStatusByTimeoutOrder(LocalDateTime time, Integer pendingPayment);


    /**
     * 获取当天的营业额
     * @param dateTimeBegin
     * @param dateTimeEnd
     * @return
     */
    @Select("select sum(amount) from orders where order_time between #{dateTimeBegin} and #{dateTimeEnd} and status=5")
    BigDecimal getTurnover(LocalDateTime dateTimeBegin,LocalDateTime dateTimeEnd);

    /**
     * 获取订单数
     * @param dateTimeBegin
     * @param dateTimeEnd
     * @return
     */
    @Select("select count(*) from orders where order_time between #{dateTimeBegin} and #{dateTimeEnd}")
    Integer getorderCount(LocalDateTime dateTimeBegin, LocalDateTime dateTimeEnd);

    /**
     * 获取有效订单数
     * @param dateTimeBegin
     * @param dateTimeEnd
     * @return
     */
    @Select("select count(*) from orders where order_time between #{dateTimeBegin} and #{dateTimeEnd} and status=5")
    Integer getvalidOrderCount(LocalDateTime dateTimeBegin, LocalDateTime dateTimeEnd);





    List<String> getTop10Name(LocalDateTime dateTimeBegin, LocalDateTime dateTimeEnd);

    List<Long> getTop10Number(LocalDateTime dateTimeBegin, LocalDateTime dateTimeEnd);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);


    Double sumByMap(Map map);
}
