package cn.com.besttone.areacode.crawling;

import cn.com.besttone.areacode.AreaDAO;
import cn.com.besttone.areacode.enums.AreaLevel;
import cn.com.besttone.areacode.model.Area;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlingAreaCode implements Crawling {

    @Autowired
    AreaDAO areaDAO;

    @Override
    public List<Object> crawlingFromWeb() {
        boolean retry = true;
        Long startTime = System.currentTimeMillis();
        String rootUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2017/";
        Document document = null;
        while (retry) {
            try {
                document = Jsoup
                        .connect(rootUrl)
                        .maxBodySize(0)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                        .get();
                retry = false;
            } catch (IOException e) {
                System.out.println("连接超时，重试。。。");
            }
        }
        retry = true;
        Area country = new Area();
        country.setCode("000000000000");
        country.setName("中华人民共和国");
        country.setLevel(AreaLevel.COUNTRY);
        Elements provinceElements = document.getElementsByAttributeValue("class", "provincetr");
        for (Element provinceElement : provinceElements) {
            Elements provinces = provinceElement.getElementsByTag("a");
            for (Element province : provinces) {
                List<Area> areas = new ArrayList<>();
                Area provinceArea = new Area();
                provinceArea.setParentCode(country.getCode());
                provinceArea.setParentName(country.getName());
                provinceArea.setName(province.text());
                provinceArea.setLevel(AreaLevel.PROVINCE);
                provinceArea.setCode(province.attr("href").replaceAll(".html", "") + "0000000000");
                areas.add(provinceArea);
                while (retry) {
                    try {
                        document = Jsoup
                                .connect(rootUrl + province.attr("href"))
                                .maxBodySize(0)
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                                .get();
                        retry = false;
                    } catch (IOException e) {
                        System.out.println("连接超时，重试。。。");
                    }
                }
                retry = true;
                Elements cityElements = document.getElementsByAttributeValue("class", "citytr");
                for (Element cityElement : cityElements) {
                    Elements cities = cityElement.getElementsByTag("a");
                    if (null == cities || cities.size() == 0) {
                        continue;
                    }
                    Area cityArea = new Area();
                    cityArea.setLevel(AreaLevel.CITY);
                    cityArea.setCode(cities.get(0).text());
                    cityArea.setName(cities.get(1).text());
                    cityArea.setParentName(provinceArea.getName());
                    cityArea.setProvinceCode(provinceArea.getCode());
                    cityArea.setParentCode(provinceArea.getCode());
                    cityArea.setProvinceName(provinceArea.getName());
                    areas.add(cityArea);
                    while (retry) {
                        try {
                            document = Jsoup
                                    .parse(new URL(rootUrl + cities.get(1).attr("href")).openConnection().getInputStream(),"gbk",rootUrl + cities.get(1).attr("href"));
                                    //.connect(rootUrl + cities.get(1).attr("href"))
                                    //.maxBodySize(0)
                                    //.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                                    //.get();
                            retry = false;
                        } catch (IOException e) {
                            System.out.println("连接超时，重试。。。");
                        }
                    }
                    retry = true;
                    Elements countyElements = document.getElementsByAttributeValue("class", "countytr");
                    for (Element countyElement : countyElements) {
                        Elements counties = countyElement.getElementsByTag("a");
                        if (null == counties || counties.size() == 0) {
                            Elements specialArea = countyElement.getElementsByTag("td");
                            if (null != specialArea && specialArea.size() > 0) {
                                Area countyArea = new Area();
                                countyArea.setLevel(AreaLevel.DISTRICT);
                                countyArea.setName(specialArea.get(1).text());
                                countyArea.setCode(specialArea.get(0).text());
                                countyArea.setParentCode(cityArea.getCode());
                                countyArea.setParentName(cityArea.getName());
                                countyArea.setProvinceName(provinceArea.getName());
                                countyArea.setProvinceCode(provinceArea.getCode());
                                countyArea.setCityCode(cityArea.getCode());
                                countyArea.setCityName(cityArea.getName());
                                areas.add(countyArea);
                            }
                            continue;
                        }
                        Area countyArea = new Area();
                        countyArea.setLevel(AreaLevel.DISTRICT);
                        countyArea.setName(counties.get(1).text());
                        countyArea.setCode(counties.get(0).text());
                        countyArea.setParentCode(cityArea.getCode());
                        countyArea.setParentName(cityArea.getName());
                        countyArea.setProvinceName(provinceArea.getName());
                        countyArea.setProvinceCode(provinceArea.getCode());
                        countyArea.setCityCode(cityArea.getCode());
                        countyArea.setCityName(cityArea.getName());
                        areas.add(countyArea);
                        while (retry) {
                            try {
                                document = Jsoup
                                        .parse(new URL(rootUrl + cities.get(1).attr("href").split("/")[0] + "/" + counties.get(1).attr("href")).openConnection().getInputStream(),"gbk",rootUrl + cities.get(1).attr("href").split("/")[0] + "/" + counties.get(1).attr("href"));
                                        //.connect(rootUrl + cities.get(1).attr("href").split("/")[0] + "/" + counties.get(1).attr("href"))
                                        //.maxBodySize(0)
                                        //.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                                        //.get();
                                retry = false;
                            } catch (IOException e) {
                                System.out.println("连接超时，重试。。。");
                            }
                        }
                        retry = true;
                        Elements townElements = document.getElementsByAttributeValue("class", "towntr");
                        for (Element townElement : townElements) {
                            Elements towns = townElement.getElementsByTag("a");
                            if (null == towns || towns.size() == 0) {
                                Elements specialArea = countyElement.getElementsByTag("td");
                                if (null != specialArea && specialArea.size() > 0) {
                                    Area townArea = new Area();
                                    townArea.setLevel(AreaLevel.STREET);
                                    townArea.setCode(specialArea.get(0).text());
                                    townArea.setName(specialArea.get(1).text());
                                    townArea.setParentName(countyArea.getName());
                                    townArea.setParentCode(countyArea.getCode());
                                    townArea.setProvinceCode(provinceArea.getCode());
                                    townArea.setProvinceName(provinceArea.getName());
                                    townArea.setCityName(cityArea.getName());
                                    townArea.setCityCode(cityArea.getCode());
                                    townArea.setDistrictCode(countyArea.getCode());
                                    townArea.setDistrictName(countyArea.getName());
                                    areas.add(townArea);
                                }
                                continue;
                            }
                            Area townArea = new Area();
                            townArea.setLevel(AreaLevel.STREET);
                            townArea.setCode(towns.get(0).text());
                            townArea.setName(towns.get(1).text());
                            townArea.setParentName(countyArea.getName());
                            townArea.setParentCode(countyArea.getCode());
                            townArea.setProvinceCode(provinceArea.getCode());
                            townArea.setProvinceName(provinceArea.getName());
                            townArea.setCityName(cityArea.getName());
                            townArea.setCityCode(cityArea.getCode());
                            townArea.setDistrictCode(countyArea.getCode());
                            townArea.setDistrictName(countyArea.getName());
                            areas.add(townArea);
                        }
                    }
                }
                System.out.println("========【" + provinceArea.getName() + "】处理结束,共【" + areas.size() + "】条记录=======");
                areaDAO.saveAll(areas);
            }
        }
        System.out.println("======总共耗时【" + (System.currentTimeMillis() - startTime) + "】ms======");
        return null;
    }

    @Override
    public void saveToDataBase() {

    }

}
