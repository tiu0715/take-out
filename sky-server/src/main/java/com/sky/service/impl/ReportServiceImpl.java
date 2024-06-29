package com.sky.service.impl;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.WatchService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {


    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end ){
       //创建列表接收
        List<LocalDate> dateList=new ArrayList<>();
        List<BigDecimal> turnoverList=new ArrayList<>();
        //当时间循环到最后一天的第二天的0.00时停止，所以让end+1
        end=end.plusDays(1);
        while (!begin.equals(end)){
            //将日期加入列表，begin作为获取营业额的当天时间
            dateList.add(begin);
            //返回当天营业额
            BigDecimal turnover=orderMapper.getTurnover(LocalDateTime.of(begin, LocalTime.MIN),LocalDateTime.of(begin, LocalTime.MAX));
            //如果turn为null，赋值0
            if (turnover==null)turnover=BigDecimal.valueOf(0);
            turnoverList.add(turnover);
            begin=begin.plusDays(1);
        }


        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //创建列表接收
        List<LocalDate> dateList=new ArrayList<>();
        List<Integer> totalUserList=new ArrayList<>();
        List<Integer> newUserList=new ArrayList<>();
        //当时间循环到最后一天的第二天的0.00时停止，所以让end+1
        end=end.plusDays(1);
        while (!begin.equals(end)){
            //将日期加入列表，begin作为获取营业额的当天时间
            dateList.add(begin);
            //返回该日期之前所有的用户数量和当天新用户数量
            Integer totalUser=userMapper.getTotalUser(LocalDateTime.of(begin, LocalTime.MAX));
            Integer newUser=userMapper.getNewUser(LocalDateTime.of(begin, LocalTime.MIN),LocalDateTime.of(begin, LocalTime.MAX));
            //如果为null，赋值0
            if (totalUser==null)totalUser=Integer.valueOf(0);
            if (newUser==null)newUser=Integer.valueOf(0);
            totalUserList.add(totalUser);
            newUserList.add(newUser);
            begin=begin.plusDays(1);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //创建列表接收
        List<LocalDate> dateList=new ArrayList<>();
        List<Integer> orderCountList=new ArrayList<>();
        List<Integer> validOrderCountList=new ArrayList<>();
        //当时间循环到最后一天的第二天的0.00时停止，所以让end+1
        end=end.plusDays(1);
        //获取总订单数和订单完成率
        Integer totalOrderCount=0;
        Integer totalValidOrderCount=0;
        while (!begin.equals(end)){
            //将日期加入列表，begin作为获取的当天时间
            dateList.add(begin);
            //返回该日期之前所有的订单数量和有效订单数量
            Integer orderCount=orderMapper.getorderCount(LocalDateTime.of(begin, LocalTime.MIN),LocalDateTime.of(begin, LocalTime.MAX));
            Integer validOrderCount=orderMapper.getvalidOrderCount(LocalDateTime.of(begin, LocalTime.MIN),LocalDateTime.of(begin, LocalTime.MAX));
            //如果为null，赋值0
            if (orderCount==null)orderCount=Integer.valueOf(0);
            if (validOrderCount==null)validOrderCount=Integer.valueOf(0);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
            totalOrderCount+=orderCount;
            totalValidOrderCount+=validOrderCount;
            begin=begin.plusDays(1);
        }
        double orderCompletionRate=totalValidOrderCount/totalOrderCount;


        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(totalValidOrderCount)
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .orderCompletionRate(orderCompletionRate)
                .build();

    }

    /**
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10Statistics(LocalDate begin, LocalDate end) {

        //创建列表接收

        List<String> nameList=orderMapper.getTop10Name(LocalDateTime.of(begin, LocalTime.MIN),LocalDateTime.of(begin, LocalTime.MAX));
        List<Long> numberList=orderMapper.getTop10Number(LocalDateTime.of(begin, LocalTime.MIN),LocalDateTime.of(begin, LocalTime.MAX));

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate now = LocalDate.now();
        //最近30天数据的起始
        LocalDateTime begin = LocalDateTime.of(now.minusDays(30), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(now.minusDays(1), LocalTime.MAX);
        //查询数据库，获取营业数据
        BusinessDataVO businessData = workspaceService.getBusinessData(begin, end);
        //读取模板
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/data_export.xlsx");
        //通过poi键数据导入Excel报表
        try {
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);

            //填充数据
            //时间，第2行
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间："+begin+"---"+end);
            //营业额，订单完成率，新增用户数，在第4行
            XSSFRow row4 = sheet.getRow(3);
            row4.getCell(2).setCellValue(businessData.getTurnover());
            row4.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row4.getCell(6).setCellValue(businessData.getNewUsers());
            //有效订单，平均客单价,第5行
            XSSFRow row5 = sheet.getRow(6);
            row5.getCell(2).setCellValue(businessData.getValidOrderCount());
            row5.getCell(4).setCellValue(businessData.getUnitPrice());

            //明细数据
            LocalDate localDate = now.minusDays(30);
            //第7行开始填充
            int rowNumber=7;
            while (!localDate.equals(now)){
                //获取当天明细数据
                BusinessDataVO data = workspaceService.getBusinessData(LocalDateTime.of(localDate, LocalTime.MIN), LocalDateTime.of(localDate, LocalTime.MAX));
                XSSFRow row = sheet.getRow(rowNumber);
                row.getCell(1).setCellValue(localDate.toString());
                row.getCell(2).setCellValue(data.getTurnover());
                row.getCell(3).setCellValue(data.getValidOrderCount());
                row.getCell(4).setCellValue(data.getOrderCompletionRate());
                row.getCell(5).setCellValue(data.getUnitPrice());
                row.getCell(6).setCellValue(data.getNewUsers());
                //循环第二天
                localDate = localDate.plusDays(1);
                rowNumber++;
            }

            //通过输出流下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            //关闭资源
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
