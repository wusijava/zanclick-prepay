package com.zanclick.prepay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 内部接口
 *
 * @author duchong
 * @date 2019-9-6 10:43:11
 */
@Api(description = "开放接口")
@Slf4j
@RestController(value = "open_controller")
@RequestMapping(value = "/api/open/")
public class OpenController extends BaseController {

    @Autowired
    private PayOrderService payOrderService;

    @ApiOperation(value = "通知补发")
    @GetMapping(value = "/resetNotify", produces = "application/json;charset=utf-8")
    public Response notifyReset(String orderNo) {
        PayOrder order = payOrderService.queryByOutTradeNo(orderNo);
        if (order == null) {
            return Response.fail("订单号有误");
        }
        if (order.isPayed()) {
            payOrderService.sendMessage(order);
        }
        return Response.ok("调用成功");
    }

    @ApiOperation(value = "退款")
    @GetMapping(value = "/refund", produces = "application/json;charset=utf-8")
    public void refund(String outTradeNo,Integer type) throws IOException {
        String authorization = getAuthorization();
        if (authorization == null) {
            return ;
        }
        if (!verifyAuthorization(authorization,"prepay:prepayAdmin")){
            return ;
        }
        if (DataUtil.isEmpty(outTradeNo) || DataUtil.isEmpty(type)){
            return ;
        }
        try {
            payOrderService.refund(outTradeNo,type);
            getResponse().setCharacterEncoding("UTF-8");
            getResponse().setContentType("application/json;charset=utf-8");
            getResponse().getWriter().write("退款成功");
        }catch (Exception e){
            log.error("退款失败:{}",e);
            getResponse().setCharacterEncoding("UTF-8");
            getResponse().setContentType("application/json;charset=utf-8");
            getResponse().getWriter().write(e.getMessage());
        }
    }


    @GetMapping(value = "/downloadExcel/{key}")
    @ResponseBody
    public void downloadExcel(@PathVariable(value = "key") String key, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object object = RedisUtil.get(key);
        ExcelDto dto = object == null ? null : (ExcelDto) object;
        if (dto == null) {
            log.error("key已经过期:{}", key);
            return;
        }
        batchExport(dto.getHeaders(), dto.getKeys(),dto.getObjectList(), request, response);
        RedisUtil.del(key);
    }

    static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
    public void batchExport(String[] headers, String[] keys, List<JSONObject> lists, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 声明一个工作薄
        XSSFWorkbook wb = new XSSFWorkbook();
        //声明一个sheet并命名
        XSSFSheet sheet = wb.createSheet("Sheet1");
        //给sheet名称一个长度
        sheet.setDefaultColumnWidth(18);
        //创建第一行（也可以称为表头）
        XSSFRow row = sheet.createRow(0);

        //给表头第一行一次创建单元格(对应字段创建对应单元格)
        for (int i = 0; i < headers.length; i++) {
            XSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            XSSFCell headerCell = row.createCell(i);
            headerCell.setCellStyle(style);
            headerCell.setCellValue(headers[i]);
        }

        int rowIndex = 0;
        XSSFCell cell = null;
        for (JSONObject obj : lists) {
            XSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            rowIndex++;
            row = sheet.createRow(rowIndex);

            for (int i = 0; i < keys.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(style);
                cell.setCellValue(obj.getString(keys[i]));
            }
        }
        String filename = "导出信息" + sdf1.format(new Date()) + ".xlsx";
        String filepath = request.getRealPath("/") + filename;
        FileOutputStream out = new FileOutputStream(filepath);
        wb.write(out);
        out.close();
        downloadExcel(filepath, response);
    }

    /**
     * 下载
     */
    public static void downloadExcel(String filepath, HttpServletResponse response)
            throws IOException {
        File file = new File(filepath);
        String fileName = file.getName();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
        response.setCharacterEncoding("utf-8");
        InputStream fis = new BufferedInputStream(new FileInputStream(file));
        byte[] b = new byte[fis.available()];
        fis.read(b);
        response.getOutputStream().write(b);
        fis.close();
        if (file.exists()) {
            file.delete();
        }
    }
}
