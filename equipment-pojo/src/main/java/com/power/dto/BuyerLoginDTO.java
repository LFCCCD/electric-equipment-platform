package com.power.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Local account login for the H5 demo client.
 */
@Data
public class BuyerLoginDTO implements Serializable {

    private String username;

    private String password;
}
