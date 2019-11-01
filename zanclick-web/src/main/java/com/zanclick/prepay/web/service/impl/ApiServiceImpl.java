package com.zanclick.prepay.web.service.impl;

import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.common.utils.POIUtil;
import com.zanclick.prepay.web.service.ApiService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author duchong
 * @description
 * @date 2019-11-1 17:44:14
 */
public class ApiServiceImpl implements ApiService {


    @Override
    public void batchImportMerchant(MultipartFile file) {

    }


    private List<RegisterMerchant> getMerchantList(MultipartFile file){
        List<RegisterMerchant> merchantList = new ArrayList<>();
        Workbook workbook = POIUtil.getWorkBook(file);
        Sheet sheet = workbook.getSheetAt(0);
        Row row;
        List<List<String>> all=new ArrayList<>();
        List<String> list=null;
        RegisterMerchant merchant = null;
        for (int i=1; i<sheet.getLastRowNum()+1;i++){
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            merchant = new RegisterMerchant();
            merchant.setAppId("");
            merchant.setContactName(POIUtil.getCellValue(row.getCell(0)));
        }
        return merchantList;
    }
}
