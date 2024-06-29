package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j

public class DishController {
    @Autowired
    private DishService dishService;


    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增菜品")
    @CacheEvict(value = "DishCache",key = "#dishDTO.categoryId")
    public Result add(@RequestBody DishDTO dishDTO){
        log.info("新增菜品{}",dishDTO);
        dishService.add(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询菜品{}",dishPageQueryDTO);
        PageResult pageResult=dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);

    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping()
    @ApiOperation("菜品批量删除")
    @CacheEvict(value = "DishCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除的菜品{}",ids);
        dishService.delete(ids);
        return Result.success();
    }

    /**
     * 根据分类查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("list")
    @ApiOperation("根据分类查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId){
        log.info("查询的分类{}",categoryId);
        List<Dish> dishs=dishService.getByCategoryId(categoryId);
        return Result.success(dishs);
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("查询的菜品{}",id);
        DishVO dishVO=dishService.getById(id);
        return Result.success(dishVO);
    }
    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("修改菜品")
    @CacheEvict(value = "DishCache",allEntries = true)
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改的菜品{}",dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }
    /**
     * 菜品启售和停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品启售和停售")
    @CacheEvict(value = "DishCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("菜品status{}",status);
        dishService.startOrStop(status,id);
        return Result.success();
    }


}
