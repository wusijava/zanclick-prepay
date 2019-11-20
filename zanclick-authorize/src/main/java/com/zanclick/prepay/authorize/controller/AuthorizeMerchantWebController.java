package com.zanclick.prepay.authorize.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.query.AuthorizeMerchantQuery;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.MerchantDetail;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.authorize.vo.web.AuthorizeWebListInfo;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.PoiUtil;
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
public class AuthorizeMerchantWebController extends BaseController {

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

    @ApiOperation(value = "商户信息详情")
    @RequestMapping(value = "detail", method = RequestMethod.POST)
    @ResponseBody
    public Response<MerchantDetail> detail(Long id) {
        AuthorizeMerchant merchant = authorizeMerchantService.queryById(id);
        if (DataUtil.isEmpty(merchant)) {
            return Response.fail("商户信息异常");
        }
        return Response.ok(getMerchantDetail(merchant));
    }

    @ApiOperation(value = "修改商户信息")
    @RequestMapping(value = "updateMerchant", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> updateMerchant(MerchantDetail merchant) {
        if (DataUtil.isEmpty(merchant) || DataUtil.isEmpty(merchant.getId()) || DataUtil.isEmpty(merchant.getWayId())) {
            return Response.fail("修改商户信息异常");
        }
        AuthorizeMerchant oldMerchant = authorizeMerchantService.queryMerchant(merchant.getMerchantNo());
        if (DataUtil.isNotEmpty(oldMerchant) && !oldMerchant.getId().equals(merchant.getId()) && !oldMerchant.isFail()) {
            return Response.fail("渠道编码重复");
        }
        try {
            authorizeMerchantService.updateMerchant(setMerchantDetail(merchant));
        } catch (BizException e) {
            log.error("修改商户信息异常:{},{}", merchant.getId(), e.getMessage());
            return Response.ok(e.getMessage());
        } catch (Exception e) {
            log.error("修改商户信息系统异常:{},{}", merchant.getId(), e);
            return Response.ok("修改失败");
        }
        return Response.ok("修改成功");
    }

    @ApiOperation(value = "导出商户信息")
    @RequestMapping(value = "batchExport", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchExport(AuthorizeMerchantQuery query) {
        List<AuthorizeMerchant> merchantList = authorizeMerchantService.queryList(query);
        List<RegisterMerchant> registerMerchantList = new ArrayList<>();
        for (AuthorizeMerchant merchant : merchantList) {
            RegisterMerchant registerMerchant = authorizeMerchantService.getRegisterMerchant(merchant);
            String reason = registerMerchant.check();
            if (reason != null){
                log.error("导入商户数据有误:{},{}",registerMerchant.getWayId(),reason);
                continue;
            }
            registerMerchantList.add(registerMerchant);
        }
        ExcelDto dto = new ExcelDto();
        dto.setHeaders(RegisterMerchant.headers);
        dto.setKeys(RegisterMerchant.keys);
        dto.setObjectList(parser(registerMerchantList));
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        RedisUtil.set(key, dto, 1000 * 60 * 30L);
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
        vo.setCreateTime(merchant.getCreateTime() == null ? null : sdf.format(merchant.getCreateTime()));
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


    /**
     * 获取详情
     *
     * @param merchant
     */
    private MerchantDetail getMerchantDetail(AuthorizeMerchant merchant) {
        MerchantDetail detail = new MerchantDetail();
        detail.setContactName(merchant.getContactName());
        detail.setContactPhone(merchant.getContactPhone());
        detail.setId(merchant.getId());
        detail.setMerchantNo(merchant.getMerchantNo());
        detail.setWayId(merchant.getWayId());
        detail.setStoreSubjectName(merchant.getStoreSubjectName());
        detail.setStoreSubjectCertNo(merchant.getStoreSubjectCertNo());
        detail.setStoreProvince(merchant.getStoreProvince());
        detail.setStoreNo(merchant.getStoreNo());
        detail.setStoreName(merchant.getStoreName());
        detail.setStoreCounty(merchant.getStoreCounty());
        detail.setStoreCity(merchant.getStoreCity());
        detail.setSellerNo(merchant.getSellerNo());
        detail.setName(merchant.getName());
        return detail;
    }

    /**
     * 设置详情
     *
     * @param merchant
     */
    private AuthorizeMerchant setMerchantDetail(MerchantDetail merchant) {
        AuthorizeMerchant detail = new AuthorizeMerchant();
        detail.setContactName(merchant.getContactName());
        detail.setContactPhone(merchant.getContactPhone());
        detail.setId(merchant.getId());
        detail.setMerchantNo("DZ" + merchant.getWayId());
        detail.setWayId(merchant.getWayId());
        detail.setStoreSubjectName(merchant.getStoreSubjectName());
        detail.setStoreSubjectCertNo(merchant.getStoreSubjectCertNo());
        detail.setStoreProvince(merchant.getStoreProvince());
        detail.setStoreNo(merchant.getStoreNo());
        detail.setStoreName(merchant.getStoreName());
        detail.setStoreCounty(merchant.getStoreCounty());
        detail.setStoreCity(merchant.getStoreCity());
        detail.setSellerNo(merchant.getSellerNo());
        detail.setName(merchant.getName());
        return detail;
    }
}
