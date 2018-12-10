package cn.com.besttone.areacode.controller;

import cn.com.besttone.areacode.crawling.Crawling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlingController {

    @Autowired
    @Qualifier(value = "crawlingAreaCode")
    Crawling crawling;


    @Autowired
    @Qualifier(value = "crawlingTelephoneCodes")
    Crawling crawlingForPhoneCode;

    @GetMapping("/area")
    public void crawlingAreaCode(){
            new Thread(() -> crawling.crawlingFromWeb()).start();
    }

    @GetMapping("/phoneCode")
    public void crawlingForPhoneCode(){
        new Thread(() -> crawlingForPhoneCode.crawlingFromWeb()).start();
    }

}
