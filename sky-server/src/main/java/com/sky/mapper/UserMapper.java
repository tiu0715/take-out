package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 插入用户
     * @param user
     */
    void insert(User user);

    /**
     * 根据openid获取用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid=#{openid}")
    User getByOpenid(String openid);

    @Select("select count(*) from user where create_time<=#{time}")
    Integer getTotalUser(LocalDateTime time);


    @Select("select count(*) from user where create_time between #{dateTimeBegin} and #{dateTimeEnd}")
    Integer getNewUser(LocalDateTime dateTimeBegin,LocalDateTime dateTimeEnd);

    Integer countByMap(Map map);
}
