package cn.com.besttone.areacode;

import cn.com.besttone.areacode.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaDAO extends JpaRepository<Area,String> {
}
