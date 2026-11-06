package com.power.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable {

    private Long equipmentId;
    private Long packageId;
    private String selectedSpec;

}
