package com.zanclick.prepay.web.service.impl;

import com.alipay.api.response.MybankCreditSupplychainFactoringSupplierCreateResponse;
import com.zanclick.prepay.authorize.entity.AuthorizeConfiguration;
import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.service.AuthorizeConfigurationService;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.util.SupplyChainUtils;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.authorize.vo.SuppilerCreate;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.POIUtil;
import com.zanclick.prepay.web.service.ApiService;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author duchong
 * @description
 * @date 2019-11-1 17:44:14
 */
@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private AuthorizeMerchantService authorizeMerchantService;
    @Autowired
    private AuthorizeConfigurationService authorizeConfigurationService;


    @Override
    public void batchImportMerchant(MultipartFile file, HttpServletRequest request) {
        List<RegisterMerchant> registerMerchantList = getMerchantList(file);
        List<RegisterMerchant> reset = new ArrayList<>();
        List<RegisterMerchant> fail = new ArrayList<>();
        for (RegisterMerchant rm:registerMerchantList){
            RegisterMerchant merchant = createAuthorizeMerchant(rm);
            if (merchant != null){
                reset.add(merchant);
            }
        }
        AuthorizeMerchant query = new AuthorizeMerchant();
        query.setState(0);
        List<AuthorizeMerchant> merchantList = authorizeMerchantService.queryList(query);
        for (AuthorizeMerchant merchant:merchantList){
            try {
                RegisterMerchant rm =  createSupplier(merchant);
                if (rm != null){
                    fail.add(rm);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    /**
     * 签约商户
     *
     * @param merchant
     * @return （原因）没有返回，则为签约成功
     */
    private RegisterMerchant createSupplier(AuthorizeMerchant merchant) {
        AuthorizeMerchant oldMerchant = authorizeMerchantService.queryByAliPayLoginNo(merchant.getSellerNo());
        if (oldMerchant != null ) {
            if (oldMerchant.getState().equals(AuthorizeMerchant.State.success.getCode())){
                merchant.setSupplierNo(oldMerchant.getSupplierNo());
                merchant.setState(AuthorizeMerchant.State.success.getCode());
                merchant.setFinishTime(new Date());
                authorizeMerchantService.updateById(merchant);
                return null;
            }else if (oldMerchant.getState().equals(AuthorizeMerchant.State.failed.getCode())){
                merchant.setReason(oldMerchant.getReason());
                merchant.setState(AuthorizeMerchant.State.failed.getCode());
                merchant.setFinishTime(new Date());
                authorizeMerchantService.updateById(merchant);
                return createRegisterMerchant(merchant);
            }
        }
        AuthorizeConfiguration configuration = authorizeConfigurationService.queryDefaultConfiguration();
        MybankCreditSupplychainFactoringSupplierCreateResponse response = SupplyChainUtils.createSupplier(create(merchant),configuration);
        if (response.isSuccess()) {
            merchant.setSupplierNo(response.getSupplierNo());
            merchant.setState(AuthorizeMerchant.State.success.getCode());
            merchant.setFinishTime(new Date());
            authorizeMerchantService.updateById(merchant);
            return null;
        } else {
            merchant.setReason(response.getSubMsg());
            merchant.setState(AuthorizeMerchant.State.failed.getCode());
            merchant.setFinishTime(new Date());
            return createRegisterMerchant(merchant);
        }
    }


    private List<RegisterMerchant> getMerchantList(MultipartFile file){
        List<RegisterMerchant> list = new ArrayList<>();
        HSSFWorkbook workbook = POIUtil.getWorkBook(file);
        if (workbook == null){
            throw new RuntimeException("导入excel出错");
        }
        HSSFSheet sheet = workbook.getSheet("Sheet1");
        if (sheet == null){
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
            qualification.setWayId(getData(row,1));
            qualification.setStoreProvince(getData(row,2));
            qualification.setStoreCity(getData(row,3));
            qualification.setStoreCounty(getData(row,4));
            qualification.setStoreNo(getData(row,5));
            qualification.setStoreName(getData(row,6));
            qualification.setStoreSubjectCertNo(getData(row,7));
            qualification.setStoreSubjectName(getData(row,8));
            qualification.setContactName(getData(row,9));
            qualification.setContactPhone(getData(row,10));
            qualification.setName(getData(row,11));
            qualification.setSellerNo(getData(row,12));
            qualification.setMerchantNo("DZ" + qualification.getWayId());
            list.add(qualification);
        }
        return list;
    }


    private RegisterMerchant createAuthorizeMerchant(RegisterMerchant dto) {
        if (judge(dto.getMerchantNo())){
            return dto;
        }
        AuthorizeMerchant merchant = new AuthorizeMerchant();
        merchant.setAppId(dto.getAppId());
        merchant.setWayId(dto.getWayId());
        merchant.setMerchantNo(dto.getMerchantNo());
        merchant.setContactName(dto.getContactName());
        merchant.setContactPhone(dto.getContactPhone());
        merchant.setCreateTime(new Date());
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
        merchant.setState(AuthorizeMerchant.State.waiting.getCode());
        authorizeMerchantService.insert(merchant);
        return null;
    }

    private Boolean judge(String merchantNo){
        AuthorizeMerchant queryMerchant = new AuthorizeMerchant();
        queryMerchant.setMerchantNo(merchantNo);
        List<AuthorizeMerchant>  merchantList = authorizeMerchantService.queryList(queryMerchant);
        if (DataUtil.isNotEmpty(merchantList)){
            for (AuthorizeMerchant merchant:merchantList){
                if (merchant.isSuccess()){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 创建商户
     *
     * @param merchant
     * @return
     */
    private SuppilerCreate create(AuthorizeMerchant merchant) {
        SuppilerCreate create = new SuppilerCreate();
        create.setStoreNo(merchant.getSellerNo());
        create.setSellerName(merchant.getName());
        create.setRcvContactEmail(null);
        create.setRcvLoginId(merchant.getSellerNo());
        create.setRcvContactName(merchant.getContactName());
        create.setRcvContactPhone(merchant.getContactPhone());
        create.setOperatorName(merchant.getOperatorName());
        create.setStoreNo(merchant.getStoreNo());
        create.setStoreName(merchant.getStoreName());
        create.setStoreSubjectName(merchant.getStoreSubjectName());
        create.setStoreSubjectCertNo(merchant.getStoreSubjectCertNo());
        create.setStoreProvince(merchant.getStoreProvince());
        create.setStoreCity(merchant.getStoreCity());
        create.setStoreCounty(merchant.getStoreCounty());
        return create;
    }


    private RegisterMerchant createRegisterMerchant(AuthorizeMerchant dto) {
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
        merchant.setState(getMerchantStateStr(dto.getState()));
        merchant.setReason(dto.getReason());
        return merchant;
    }


    private String getMerchantStateStr(Integer state){
        if (AuthorizeMerchant.State.waiting.getCode().equals(state)){
            return "等待";
        }else if (AuthorizeMerchant.State.success.getCode().equals(state)){
            return "成功";
        }else if (AuthorizeMerchant.State.failed.getCode().equals(state)){
            return "失败";
        }else {
            return "未知";
        }
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
