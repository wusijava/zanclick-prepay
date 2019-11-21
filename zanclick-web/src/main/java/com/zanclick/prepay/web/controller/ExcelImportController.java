package com.zanclick.prepay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.PoiUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.user.query.UserQuery;
import com.zanclick.prepay.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author duchong
 * @description
 * @date
 */
@Api(description = "商户管理系统接口")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/excel/import")
public class ExcelImportController {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private UserService userService;

    @Value("${excelDownloadUrl}")
    private String excelDownloadUrl;


    @ApiOperation(value = "导入商户信息")
    @RequestMapping(value = "batchImportMerchant", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchImportMerchant(MultipartFile file) {
        try {
            List<RegisterMerchant> registerMerchantList = getMerchantList(file);
            ExcelDto dto = new ExcelDto();
            dto.setHeaders(RegisterMerchant.headers);
            dto.setKeys(RegisterMerchant.keys);
            dto.setObjectList(parser(registerMerchantList));
            String key = UUID.randomUUID().toString().replaceAll("-", "");
            RedisUtil.set(key, dto, 1000 * 60 * 30L);
            String url = excelDownloadUrl + key;
            return Response.ok(url);
        } catch (Exception e) {
            log.error("导入商户出错:{}", e);
            return Response.fail("导入商户失败");
        }
    }


    private List<JSONObject> parser(List<RegisterMerchant> merchantList) {
        return JSONObject.parseArray(JSONObject.toJSONString(merchantList), JSONObject.class);
    }

    /**
     * 获取导入的数据
     *
     * @param file
     */
    private List<RegisterMerchant> getMerchantList(MultipartFile file) {
        List<RegisterMerchant> list = new ArrayList<>();
        HSSFWorkbook workbook = PoiUtil.getWorkBook(file);
        if (workbook == null) {
            throw new RuntimeException("导入excel出错");
        }
        HSSFSheet sheet = workbook.getSheet("Sheet1");
        if (sheet == null) {
            throw new RuntimeException("导入excel出错");
        }
        Integer rowNum = sheet.getLastRowNum();
        RegisterMerchant qualification = null;
        for (int i = 1; i <= rowNum; i++) {
            qualification = new RegisterMerchant();
            qualification.setAppId("201910091625131208151");
            qualification.setOperatorName("中国移动");
            HSSFRow row = sheet.getRow(i);
            format(row);
            qualification.setWayId(getData(row, 1));
            qualification.setStoreProvince(getData(row, 2));
            qualification.setStoreCity(getData(row, 3));
            qualification.setStoreCounty(getData(row, 4));
            qualification.setStoreNo(getData(row, 5));
            qualification.setStoreName(getData(row, 6));
            qualification.setStoreSubjectCertNo(getData(row, 7));
            qualification.setStoreSubjectName(getData(row, 8));
            qualification.setContactName(getData(row, 9));
            qualification.setContactPhone(getData(row, 10));
            qualification.setName(getData(row, 11));
            qualification.setSellerNo(getData(row, 12));
            qualification.setMerchantNo("DZ" + qualification.getWayId());
            try {
                AuthorizeMerchant merchant = authorizeMerchantService.createMerchant(qualification);
                UserQuery user = userService.createUser(merchant.getSellerNo(),merchant.getStoreSubjectName(),merchant.getStoreName(),merchant.getWayId(),merchant.getContactPhone());
                merchant.setStoreMarkCode(user.getStoreMarkCode());
                merchant.setUid(user.getUid());
                authorizeMerchantService.updateById(merchant);
                qualification.setPassword(user.getPwd());
            }catch (BizException e){
                log.error("创建商户失败:{},{},{}",qualification.getWayId(),qualification.getStoreName(),e);
                qualification.setState("签约失败");
                qualification.setReason(e.getMessage());
            }catch (Exception e){
                log.error("创建商户异常:{},{},{}",qualification.getWayId(),qualification.getStoreName(),e);
                qualification.setState("签约失败");
                qualification.setReason("系统异常");
            }
            list.add(qualification);
        }
        return list;
    }

    /**
     * 格式化excel数据
     *
     * @param row
     */
    private void format(HSSFRow row) {
        row.getCell(1).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(3).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(5).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(6).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(7).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(8).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(9).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(10).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(11).setCellType(Cell.CELL_TYPE_STRING);
        row.getCell(12).setCellType(Cell.CELL_TYPE_STRING);
    }

    /**
     * 获取数据
     *
     * @param row
     * @param cellNum
     */
    private String getData(HSSFRow row, Integer cellNum) {
        return format(row.getCell(cellNum).getStringCellValue());

    }

    /**
     * 格式化数据
     *
     * @param s
     */
    private String format(String s) {
        if (DataUtil.isEmpty(s)) {
            return "";
        }
        return s.trim().replaceAll("\r", "").replaceAll("\n", "");
    }

}
