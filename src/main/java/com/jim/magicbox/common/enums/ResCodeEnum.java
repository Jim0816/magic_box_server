package com.jim.magicbox.common.enums;

/**
 * @ClassName ResData
 * @Description 返回状态枚举
 * @Author Jim
 * @Date 2022/2/22 16:17
 **/
public enum ResCodeEnum {
    // 通用
    SUCCESS(200, "SUCCESS"),
    FAILED(500, "FAILED");



    private final long code;
    private final String msg;

    ResCodeEnum(long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ResCodeEnum getResCode(long code){
        ResCodeEnum[] resCodeEnums = ResCodeEnum.values();
        for (ResCodeEnum resCodeEnum : resCodeEnums) {
            if (resCodeEnum.getCode() == code){
                return resCodeEnum;
            }
        }
        return null;
    }

}
