package cn.com.besttone.areacode;

import cn.com.besttone.areacode.model.PhoneCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneCodeDAO extends JpaRepository<PhoneCode,String> {
}
