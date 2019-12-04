package com.zanclick.prepay.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.query.AuthorizeMerchantQuery;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.common.entity.ExcelDto;
import com.zanclick.prepay.common.entity.RequestContext;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.exception.BizException;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.DateUtil;
import com.zanclick.prepay.common.utils.PoiUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import com.zanclick.prepay.order.entity.Area;
import com.zanclick.prepay.order.mapper.AreaMapper;
import com.zanclick.prepay.order.service.AreaService;
import com.zanclick.prepay.user.entity.User;
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

import java.util.*;

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
    @Autowired
    private AreaService areaService;

    @Value("${excelDownloadUrl}")
    private String excelDownloadUrl;

    static Map<String,User> userMap = null;

    @ApiOperation(value = "导入商户信息")
    @RequestMapping(value = "batchImportMerchant", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchImportMerchant(MultipartFile file) {
        try {
            List<RegisterMerchant> registerMerchantList =getMerchantList(file);
            if(registerMerchantList==null){
                return Response.fail("请正确填写省市!");
            }
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

    @ApiOperation(value = "导出商户信息")
    @RequestMapping(value = "batchExportMerchant", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> batchExportMerchant(AuthorizeMerchantQuery query) {
        RequestContext.RequestUser user = RequestContext.getCurrentUser();
        if (user.getType().equals(1)) {
            query.setUid(user.getUid());
        } else if (user.getType().equals(2)) {
            query.setStoreMarkCode(user.getStoreMarkCode());
        }
        List<AuthorizeMerchant> merchantList = authorizeMerchantService.queryList(query);
        List<RegisterMerchant> registerMerchantList = new ArrayList<>();
        for (AuthorizeMerchant merchant : merchantList) {
            RegisterMerchant registerMerchant = getRegisterMerchant(merchant);
//            String reason = registerMerchant.check();
//            if (reason != null) {
//                continue;
//            }
            registerMerchantList.add(registerMerchant);
        }
        userMap = null;
        ExcelDto dto = new ExcelDto();
        dto.setHeaders(RegisterMerchant.headers);
        dto.setKeys(RegisterMerchant.keys);
        dto.setObjectList(parser(registerMerchantList));
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        RedisUtil.set(key, dto, 1000 * 60 * 30L);
        String url = excelDownloadUrl + key;
        return Response.ok(url);
    }


    private RegisterMerchant getRegisterMerchant(AuthorizeMerchant dto) {
        RegisterMerchant merchant = new RegisterMerchant();
        merchant.setAppId(dto.getAppId());
        merchant.setWayId(dto.getWayId());
        merchant.setMerchantNo(dto.getMerchantNo());
        merchant.setContactName(dto.getContactName());
        merchant.setContactPhone(dto.getContactPhone());
        merchant.setName(dto.getName());
        merchant.setOperatorName(dto.getOperatorName());
        merchant.setStoreSubjectName(dto.getStoreSubjectName());
        merchant.setStoreSubjectCertNo(dto.getStoreSubjectCertNo());
        merchant.setStoreNo(dto.getStoreNo());
        merchant.setStoreName(dto.getStoreName());
        merchant.setStoreProvince(dto.getStoreProvince());
        merchant.setStoreCity(dto.getStoreCity());
        merchant.setStoreCounty(dto.getStoreCounty());
        merchant.setStoreProvinceCode(dto.getStoreProvinceCode());
        merchant.setStoreCityCode(dto.getStoreCityCode());
        merchant.setStoreCountyCode(dto.getStoreCountyCode());
        merchant.setSellerNo(dto.getSellerNo());
        merchant.setState(dto.getStateDesc());
        merchant.setReason(dto.getReason());
        merchant.setCreateTime(DateUtil.formatDate(dto.getCreateTime(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
        if (dto.getUid() != null){
            User user = getUser(dto.getUid());
            merchant.setPassword(user == null ? null : user.getPwd());
        }
        return merchant;
    }


    private List<JSONObject> parser(List<RegisterMerchant> merchantList) {
        return JSONObject.parseArray(JSONObject.toJSONString(merchantList), JSONObject.class);
    }

    /**
     * 获取用户数据
     * @param uid
     * */
    private User getUser(String uid){
        if (userMap == null){
            userMap = new HashMap<>();
            List<User> userList = userService.queryList(new UserQuery());
            for (User user:userList){
                userMap.put(user.getUid(),user);
            }
        }
        return userMap.get(uid);
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
        //省
      Area area=new Area();
        //市
        Area area3=new Area();
        for (int i = 1; i <= rowNum; i++) {
            qualification = new RegisterMerchant();
            HSSFRow row = sheet.getRow(i);
            format(row);
            //判断省
            String province=getData(row, 2);
            String city=getData(row, 3);
            if(province==""||city==""){
                log.error("省市不能为空!");
                return null;}
            area.setName(province);
            area3.setName(city);
            //模糊查询省
            Area area1=areaService.selectByName(area);
            if(area1!=null&&area1.getLevel().equals("1")){
                //查到省 再判断市
                Area area2=areaService.selectByName(area3);
                if(area2==null){
                    area2=new Area();
                    area2.setParentCode("0");
                }
                if(area1.getCode().equals(area2.getParentCode())){
                    //省市匹配
                    qualification.setStoreProvince(area1.getName());
                    qualification.setStoreProvinceCode(area1.getCode());
                    qualification.setStoreCity(area2.getName());
                    qualification.setAppId("201910091625131208151");
                    qualification.setOperatorName("中国移动");
                    qualification.setStoreCityCode(area2.getCode());
                    qualification.setWayId(getData(row, 1));
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
                    qualification.setCreateTime(DateUtil.formatDate(new Date(), DateUtil.PATTERN_YYYY_MM_DD_HH_MM_SS));
                }else{
                   Response.fail("省市不匹配!");
                   log.error("省市不匹配!");
                   return null;
                }
            }else{
                Response.fail("省份填写错误!");
                log.error("省份填写错误!");
                return null;
            }

            try {
                AuthorizeMerchant merchant = authorizeMerchantService.createMerchant(qualification);
                User user = userService.createUser(merchant.getSellerNo(),merchant.getStoreSubjectName(),merchant.getStoreName(),merchant.getWayId(),merchant.getContactPhone());
                merchant.setStoreMarkCode(user.getStoreMarkCode());
                merchant.setUid(user.getUid());
                authorizeMerchantService.updateById(merchant);
                qualification.setPassword(user.getPwd());
                qualification.setState(merchant.getStateDesc());
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
