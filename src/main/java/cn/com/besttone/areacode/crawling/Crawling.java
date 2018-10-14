package cn.com.besttone.areacode.crawling;

import java.util.List;

public interface Crawling {
    List<Object> crawlingFromWeb();
    void saveToDataBase();
}
