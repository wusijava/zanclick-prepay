package com.zanclick.prepay.setmeal.query;

import com.zanclick.prepay.setmeal.entity.SetMeal;
import lombok.Data;

/**
 * 套餐信息
 * @author duchong
 * @date 2019-7-8 14:19:20
 */
@Data
public class SetMealQuery extends SetMeal {

    private Integer page;

    private Integer limit;

    private Integer offset;

    private String startTime;

    private String endTime;

}
