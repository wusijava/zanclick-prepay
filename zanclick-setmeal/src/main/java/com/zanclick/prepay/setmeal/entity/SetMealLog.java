package com.zanclick.prepay.setmeal.entity;

import com.zanclick.prepay.common.entity.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetMealLog implements Identifiable<Long> {
    private Long id;

    /**
     * 用户id
     */
    private String userId;


    private String ip;

    private  String title;

    private  String createTime;

    private Integer state;

}
