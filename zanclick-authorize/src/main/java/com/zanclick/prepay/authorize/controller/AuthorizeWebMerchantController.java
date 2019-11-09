package com.zanclick.prepay.authorize.controller;

import com.alipay.api.response.MybankCreditSupplychainFactoringSupplierCreateResponse;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.query.AuthorizeMerchantQuery;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.util.SupplyChainUtils;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.authorize.vo.SuppilerCreate;
import com.zanclick.prepay.authorize.vo.web.AuthorizeWebListInfo;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.POIUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author duchong
 * @date 2019-11-9 14:51:47
 **/
@Api(description = "商户管理系统接口")
@Slf4j
@RestController
@RequestMapping(value = "/api/web/authorize/merchant")
public class AuthorizeWebMerchantController extends BaseController {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;

    @Value("${excelDownloadUrl}")
    private String excelDownloadUrl;

    @ApiOperation(value = "商户列表")
    @PostMapping(value = "/list")
    @ResponseBody
    public Response<Page<AuthorizeWebListInfo>> list(AuthorizeMerchantQuery query) {
        if (DataUtil.isEmpty(query.getPage())) {
            query.setPage(0);
        }
        if (DataUtil.isEmpty(query.getLimit())) {
            query.setLimit(10);
        }
        Pageable pageable = PageRequest.of(query.getPage(), query.getLimit());
        Page<AuthorizeMerchant> page = authorizeMerchantService.queryPage(query, pageable);
        List<AuthorizeWebListInfo> voList = new ArrayList<>();
        for (AuthorizeMerchant merchant : page.getContent()) {
            voList.add(getListVo(merchant));
        }
        Page<AuthorizeWebListInfo> voPage = new PageImpl<>(voList, pageable, page.getTotalElements());
        return Response.ok(voPage);
    }


    @ApiOperation(value = "导入商户信息")
    @RequestMapping(value = "batchExport", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchExport(AuthorizeMerchantQuery query) {
        List<AuthorizeMerchant> merchantList = authorizeMerchantService.queryList(query);
        List<RegisterMerchant> registerMerchantList = new ArrayList<>();
        for (AuthorizeMerchant merchant : merchantList) {
            registerMerchantList.add(authorizeMerchantService.getRegisterMerchant(merchant));
        }
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        RedisUtil.set(key, registerMerchantList, 1000 * 60 * 30L);
        String url = excelDownloadUrl + key;
        return Response.ok(url);
    }

    @ApiOperation(value = "导入商户信息")
    @RequestMapping(value = "batchImport", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchImport(MultipartFile file) {
        try {
            authorizeMerchantService.createMerchantList(getMerchantList(file));
            List<RegisterMerchant> registerMerchantList = authorizeMerchantService.createAllSupplier();
            String key = UUID.randomUUID().toString().replaceAll("-", "");
            RedisUtil.set(key, registerMerchantList, 1000 * 60 * 30L);
            String url = excelDownloadUrl + key;
            return Response.ok(url);
        } catch (Exception e) {
            log.error("导入商户出错:{}",e);
            return Response.fail("导入商户失败");
        }
    }


    /**
     * 获取显示Modal
     *
     * @param record
     * @return
     */
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private AuthorizeWebListInfo getListVo(AuthorizeMerchant merchant) {
        AuthorizeWebListInfo vo = new AuthorizeWebListInfo();
        vo.setId(merchant.getId());
        vo.setMerchantNo(merchant.getMerchantNo());
        vo.setWayId(merchant.getWayId());
        vo.setCreateTime(sdf.format(merchant.getCreateTime()));
        vo.setContactName(merchant.getContactName());
        vo.setContactPhone(merchant.getContactPhone());
        vo.setName(merchant.getName());
        vo.setReason(merchant.getReason());
        vo.setStoreSubjectName(merchant.getStoreSubjectName());
        vo.setStoreSubjectCertNo(merchant.getStoreSubjectCertNo());
        vo.setStoreProvince(merchant.getStoreProvince());
        vo.setStoreNo(merchant.getStoreNo());
        vo.setStoreName(merchant.getStoreName());
        vo.setStoreCounty(merchant.getStoreCounty());
        vo.setStoreCity(merchant.getStoreCity());
        vo.setSellerNo(merchant.getSellerNo());
        vo.setState(merchant.getState());
        vo.setStateStr(merchant.getStateDesc());
        return vo;
    }

    /**
     * 获取导入的数据
     *
     * @param file
     */
    private List<RegisterMerchant> getMerchantList(MultipartFile file) {
        List<RegisterMerchant> list = new ArrayList<>();
        HSSFWorkbook workbook = POIUtil.getWorkBook(file);
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
            HSSFRow row = workbook.getSheet("Sheet1").getRow(i);
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
