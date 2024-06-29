package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {

        //判断加入的商品是否存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //获取userId
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //如果存在，则表的number+1
        if(list!=null&&list.size()>0){
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumberById(cart);
        }

        //不存在，插入购物车数据

        //判断添加到购物车的是菜品还是套餐
        if(shoppingCart.getDishId()!=null){
            //菜品id不是空，则插入的是菜品
            DishVO dishVO = dishMapper.getById(shoppingCart.getDishId());


            shoppingCart.setName(dishVO.getName());
            shoppingCart.setAmount(dishVO.getPrice());
            shoppingCart.setImage(dishVO.getImage());
        }else {
            //插入的是套餐
            Long setmealId = shoppingCart.getSetmealId();
            Setmeal setmeal = setmealMapper.getById(setmealId);

            shoppingCart.setName(setmeal.getName());
            shoppingCart.setAmount(setmeal.getPrice());
            shoppingCart.setImage(setmeal.getImage());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());

        shoppingCartMapper.insert(shoppingCart);


    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {

        //获取微信用户id
        Long userId = BaseContext.getCurrentId();

        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        shoppingCartMapper.delete(shoppingCart);

    }

    /**
     * 减少购物车
     * @param shoppingCartDTO
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(userId);

        //判断购物车目标商品数量，大于1，则-1
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        ShoppingCart cart=list.get(0);
        if(cart.getNumber()>1){
            cart.setNumber(cart.getNumber()-1);
            shoppingCartMapper.updateNumberById(cart);
        }else {
            //否则，删除该购物车
            shoppingCartMapper.delete(shoppingCart);
        }


    }
}
