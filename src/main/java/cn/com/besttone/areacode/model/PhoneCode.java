package cn.com.besttone.areacode.model;

import cn.com.besttone.areacode.enums.PhoneCodeType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "phone_code")
public class PhoneCode {
    @Id
    private String code;

    private PhoneCodeType type;

    private String province;

    private String city;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PhoneCodeType getType() {
        return type;
    }

    public void setType(PhoneCodeType type) {
        this.type = type;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
