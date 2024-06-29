package com.sky.controller.admin;

import com.sky.result.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shop")
@Api(tags = "店铺操作接口")
@Slf4j
public class ShopController {
    private static String SHOP_STATUS="SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 店铺状态设置
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("店铺状态设置")
    public Result setStatus(@PathVariable Integer status){
        log.info("店铺状态{}",status);
        redisTemplate.opsForValue().set(SHOP_STATUS,status);
        return Result.success();

    }

    /**
     * 获取店铺状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);
        return Result.success(status);
    }

}
