package cn.com.besttone.areacode.controller;

import cn.com.besttone.areacode.crawling.Crawling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlingController {

    @Autowired
    Crawling crawling;

    @GetMapping("/area")
    public void crawlingAreaCode(){
            new Thread(() -> crawling.crawlingFromWeb()).start();
    }

}
