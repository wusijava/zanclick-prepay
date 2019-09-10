package com.zanclick.prepay.common.entity;

import lombok.Data;

/**
 * @author duchong
 * @description
 * @date
 */
@Data
public class KeyValue {
    private String key;

    private String value;

    public KeyValue() {
    }

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
