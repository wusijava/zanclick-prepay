package com.zanclick.prepay.web.controller;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.POIUtil;
import com.zanclick.prepay.order.entity.PayOrder;
import com.zanclick.prepay.order.service.PayOrderService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 内部接口
 *
 * @author duchong
 * @date 2019-9-6 10:43:11
 */
@Slf4j
@RestController(value = "open_controller")
@RequestMapping(value = "/api/open/")
public class OpenController {

    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;

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



    @ApiOperation(value = "导入商户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "加密后的摘要,每次请求后由服务端生成,通过Header返回", required = true, dataType = "String", paramType = "header")
    })
    @RequestMapping(value = "importMerchantInfo", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> importMerchantInfo(MultipartFile file){
        List<AuthorizeMerchant> list = new ArrayList<>();
        try{
        //得到文件
        Workbook Workbook= POIUtil.getWorkBook(file);
        //得到sheet
        Sheet sheet =Workbook.getSheetAt(0);
        Row row;
        String wayId,storeProvince,storeCity,storeCounty,storeNo,storeName,storeSubjectCertNo,storeSubjectName,contactName,
                contactPhone,name,sellerId;

        for (int i = 1; i < sheet.getLastRowNum()+1; i++) {
            AuthorizeMerchant authorizeMerchant=new AuthorizeMerchant();
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            wayId = POIUtil.getCellValue(row.getCell(1));
            authorizeMerchant.setWayId(wayId);
            storeProvince = POIUtil.getCellValue(row.getCell(2));
            authorizeMerchant.setStoreProvince(storeProvince);
            storeCity = POIUtil.getCellValue(row.getCell(3));
            authorizeMerchant.setStoreCity(storeCity);
            storeCounty = POIUtil.getCellValue(row.getCell(4));
            authorizeMerchant.setStoreCounty(storeCounty);
            storeNo = POIUtil.getCellValue(row.getCell(5));
            authorizeMerchant.setStoreNo(storeNo);
            storeName = POIUtil.getCellValue(row.getCell(6));
            authorizeMerchant.setStoreName(storeName);
            storeSubjectCertNo = POIUtil.getCellValue(row.getCell(7));
            authorizeMerchant.setStoreSubjectCertNo(storeSubjectCertNo);
            storeSubjectName = POIUtil.getCellValue(row.getCell(8));
            authorizeMerchant.setStoreSubjectName(storeSubjectName);
            contactName = POIUtil.getCellValue(row.getCell(9));
            authorizeMerchant.setContactName(contactName);
            contactPhone = POIUtil.getCellValue(row.getCell(10));
            authorizeMerchant.setContactPhone(contactPhone);
            name = POIUtil.getCellValue(row.getCell(11));
            authorizeMerchant.setName(name);
            sellerId = POIUtil.getCellValue(row.getCell(12));
            authorizeMerchant.setSellerId(sellerId);
            authorizeMerchant.setFinishTime(new Date());
            authorizeMerchant.setAppId("201910091625131208151");
            authorizeMerchant.setMerchantNo("DZ"+wayId);
            authorizeMerchant.setOperatorName("中国移动");
            authorizeMerchant.setState(AuthorizeMerchant.State.success.getCode());

           list.add(authorizeMerchant);
         }
            authorizeMerchantService.createMerchantByExcel(list);
             return  Response.ok("导入商户成功");
        }catch (Exception e){
            log.error("导入商户出错");
            return Response.fail("导入商户失败");
        }
    }
}
