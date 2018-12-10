package cn.com.besttone.areacode.enums;

public enum PhoneCodeType {
    TELE("固话"), CMCC("中国移动"), CUCC("中国联通"), CTCC("中国电信");

    String name;

    PhoneCodeType(String name){
        this.name = name;
    }

}
