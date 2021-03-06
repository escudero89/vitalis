package com.rocket.vitalis.dto;

import com.rocket.vitalis.model.MeasurementType;
import lombok.Data;

/**
 * Created by sscotti on 8/11/16.
 */
@Data
public class MeasureRequest {
    private Long idModule;
    private String measureDate;
    private MeasurementType measureName;
    private Double value;
    private Double valueSecondary;
}
