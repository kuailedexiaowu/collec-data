package cn.com.besttone.areacode.crawling;

import cn.com.besttone.areacode.PhoneCodeDAO;
import cn.com.besttone.areacode.enums.PhoneCodeType;
import cn.com.besttone.areacode.model.PhoneCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlingTelephoneCodes implements Crawling {

    @Autowired
    PhoneCodeDAO phoneCodeDAO;
    @Override
    public List<Object> crawlingFromWeb() {
        Long startTime = System.currentTimeMillis();
        crawlingForTele();
        System.out.println("======采集固话总共耗时【" + (System.currentTimeMillis() - startTime) + "】ms======");
        startTime = System.currentTimeMillis();
        crawlingForMobile();
        System.out.println("======采集手机号总共耗时【" + (System.currentTimeMillis() - startTime) + "】ms======");
        return null;
    }

    @Override
    public void saveToDataBase() {

    }

    private void crawlingForTele(){
        String rootUrl = "http://www.zou114.com/qh/";
        Document document = null;
        try{
            document = Jsoup.connect(rootUrl).maxBodySize(0).get();
        }catch (IOException e){
            e.printStackTrace();
        }
        Element provinceRootElement = document.getElementsByAttributeValue("class","more50").last();
        Elements provinces = provinceRootElement.getElementsByTag("a");
        for(Element provinceElement : provinces){
            List<PhoneCode> provincePhoneCodes = new ArrayList<>();
            String provinceName = provinceElement.text();
            String cityUrl = provinceElement.attr("href");

            try{
                document = Jsoup.connect(rootUrl + "/" + cityUrl).maxBodySize(0).get();
            }catch (IOException e){
                e.printStackTrace();
            }
            Element cityRootElement = document.getElementsByAttributeValue("class","nrbnr").first();
            Elements cities = cityRootElement.getElementsByAttributeValue("color","green");
            for(Element cityElement : cities){
                String[] fields = cityElement.text().split(" ");
                PhoneCode phoneCode = new PhoneCode();
                phoneCode.setType(PhoneCodeType.TELE);
                phoneCode.setProvince(provinceName);
                phoneCode.setCity(fields[3]);
                phoneCode.setCode(fields[1]);
                if(fields[3].equals("市辖区")){
                    phoneCode.setCity(provinceName);
                    phoneCodeDAO.save(phoneCode);
                    break;
                }
                provincePhoneCodes.add(phoneCode);
            }
            phoneCodeDAO.saveAll(provincePhoneCodes);
        }
    }

    private void crawlingForMobile(){
        String rootUrl = "http://www.sjgsd.com";
        Document document = null;
        try{
            document = Jsoup.connect(rootUrl).maxBodySize(0).get();
        }catch (IOException e){
            e.printStackTrace();
        }
        Elements haoduanElements = document.getElementsByAttributeValueStarting("href","/l");
        for(Element haoduanElement : haoduanElements){
            String haoduanUrl = haoduanElement.attr("href");
            try{
                document = Jsoup.connect(rootUrl + haoduanUrl).maxBodySize(0).get();
            }catch (IOException e){
                e.printStackTrace();
            }
            Elements provinceElements = document.getElementsByAttributeValueStarting("class","wrap h_list").first().getElementsByAttributeValueStarting("href","/l");
            List<PhoneCode> phoneCodes = new ArrayList<>();
            for(Element provinceElement : provinceElements){
                String cityUrl = provinceElement.attr("href");
                try{
                    document = Jsoup.connect(rootUrl + cityUrl).maxBodySize(0).get();
                }catch (IOException e){
                    e.printStackTrace();
                }
                Elements citiesElements = document.getElementsByTag("tr");
                citiesElements.remove(0);
                for (Element cityElement : citiesElements){
                    Elements phoneElements = cityElement.getElementsByTag("td");
                    PhoneCode phoneCode = new PhoneCode();
                    phoneCode.setCity(phoneElements.get(2).text());
                    phoneCode.setProvince(phoneElements.get(1).text());
                    phoneCode.setCode(phoneElements.get(0).text());
                    String value = phoneElements.get(5).text();
                    if(value.equals(PhoneCodeType.CUCC.name())){
                        phoneCode.setType(PhoneCodeType.CUCC);
                    }else if(value.equals(PhoneCodeType.CMCC)){
                        phoneCode.setType(PhoneCodeType.CMCC);
                    }else {
                        phoneCode.setType(PhoneCodeType.CTCC);
                    }
                    phoneCodes.add(phoneCode);
                }
            }
            phoneCodeDAO.saveAll(phoneCodes);
            System.out.println("===============" + phoneCodes.size() + "================");
        }
    }
}
