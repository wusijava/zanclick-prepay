package com.zanclick.prepay.web.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author duchong
 * @description
 * @date 2019-11-1 17:44:20
 */
public interface ApiService {

    /**
     * 批量导入商户信息
     *
     * @param file
     */
    void batchImportMerchant(MultipartFile file);
}
