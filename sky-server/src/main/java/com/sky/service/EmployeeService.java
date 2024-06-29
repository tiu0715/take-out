package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 添加员工
     * @param employeeDTO
     */
    void add(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 员工禁用和启用
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id获取员工
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 更新员工
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);

    void editPassword(PasswordEditDTO passwordEditDTO);
}
