package com.zanclick.prepay.setmeal.query;

import com.zanclick.prepay.setmeal.entity.SetMeal;
import lombok.Data;

@Data
public class SetMealQuery extends SetMeal {
    private Integer page;

    private Integer limit;

    private Integer offset;
    private String startTime;

    private String endTime;
}
