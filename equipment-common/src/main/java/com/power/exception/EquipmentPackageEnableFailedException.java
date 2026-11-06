package com.power.exception;

/**
 * 套餐启用失败异常
 */
public class EquipmentPackageEnableFailedException extends BaseException {

    public EquipmentPackageEnableFailedException(){}

    public EquipmentPackageEnableFailedException(String msg){
        super(msg);
    }
}
