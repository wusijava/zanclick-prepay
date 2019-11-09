package com.zanclick.prepay.authorize.controller;

import com.zanclick.prepay.authorize.entity.AuthorizeMerchant;
import com.zanclick.prepay.authorize.query.AuthorizeMerchantQuery;
import com.zanclick.prepay.authorize.service.AuthorizeMerchantService;
import com.zanclick.prepay.authorize.vo.RegisterMerchant;
import com.zanclick.prepay.authorize.vo.web.AuthorizeWebListInfo;
import com.zanclick.prepay.common.base.controller.BaseController;
import com.zanclick.prepay.common.entity.Response;
import com.zanclick.prepay.common.utils.DataUtil;
import com.zanclick.prepay.common.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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
        for (AuthorizeMerchant merchant:merchantList){
            registerMerchantList.add(getExcelDetail(merchant));
        }
        String key = UUID.randomUUID().toString().replaceAll("-","");
        RedisUtil.set(key,registerMerchantList,1000*60*30L);
        String url=excelDownloadUrl+key;
        return Response.ok(url);
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
        vo.setStateStr(getStateDesc(merchant.getState()));
        return vo;
    }

    /**
     * 获取显示的状态信息
     *
     * @param state
     */
    private String getStateDesc(Integer state) {
        if (AuthorizeMerchant.State.success.getCode().equals(state)) {
            return "签约成功";
        } else if (AuthorizeMerchant.State.failed.getCode().equals(state)) {
            return "签约失败";
        } else {
            return "等待签约";
        }
    }

    private RegisterMerchant getExcelDetail(AuthorizeMerchant dto) {
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
        merchant.setState(getStateDesc(dto.getState()));
        merchant.setReason(dto.getReason());
        return merchant;
    }


}
