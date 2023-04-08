package com.example.FrantsevParser.services;

import com.example.FrantsevParser.model.Information;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class Parser {
    WebDriver driver;
    public void enableConnection(WebDriver drivers) {
        driver = drivers;
    }
    public WebDriver getConnection(){
        return driver;
    }
    private WebDriver searchRequest(String data) throws InterruptedException {
        WebDriver conDriver = getConnection();
        conDriver.get("https://rozetka.com.ua/");

        WebElement input = conDriver.findElement(By.cssSelector("input[name='search']"));
        WebElement btn = conDriver.findElement(By.cssSelector("button[class='button button_color_green button_size_medium search-form__submit ng-star-inserted']"));

        input.sendKeys(data);
        btn.click();

        Thread.sleep(2000);

        return conDriver;
    }
    private int countUp(WebDriver conDriver) {
        List<WebElement> elems = conDriver.findElements(By.className("pagination__item"));

        if (elems.size() == 0) {
            return 0;
        } else {
            return Integer.parseInt(elems.get(elems.size() - 1).getText());
        }
    }
    private List<Information> writeDataIntoList(Elements adElems, List<Information> informationList, int num, String data) {
        for (Element product : adElems) {
            Information foundInfo = new Information();

            foundInfo.setNum(num);

            foundInfo.setSearch(data);

            foundInfo.setIntNum(product.select("div.g-id.display-none").text());

            foundInfo.setDesc(product.select("a.goods-tile__heading").text());

            String price = product.select("span.goods-tile__price-value").text();

            foundInfo.setPrice(price.isEmpty()? "Помилка! - на сайті не має такого товару!" : price);

            foundInfo.setAvail(product.select("div.goods-tile__availability").text());

            foundInfo.setLink(product.select("a.goods-tile__heading").attr("href"));

            informationList.add(foundInfo);
        }

        return informationList;
    }
    private List<Information> writeInfoFromPages(List<Information> informationPage, WebDriver conDriver, int num, String data, int countUp) {
        double perc = generatedPerc(countUp);
        while (true) {
            try {
                WebElement nextBtn = conDriver.findElement(By.cssSelector("a.pagination__direction--forward"));

                if (nextBtn.getAttribute("class").contains("disabled")) {
                    break;
                }

                nextBtn.click();

                Thread.sleep(2000);

                Document document = Jsoup.parse(conDriver.getPageSource());

                Elements elem = document.select("div.goods-tile");

                Thread.sleep(2000);

                num = num + 1;

                informationPage = writeDataIntoList(elem, informationPage, num, data);

                informationParser(perc += generatedPerc(countUp));

            } catch (Exception exc) {
                break;
            }
        }

        return informationPage;
    }
    private double generatedPerc(int countUp) {
        return 100 / countUp;
    }
    private void outputInformationToConsole(int countUp) {
        System.out.println("Знайдено сторінок: " + (countUp == 0 ? 1 : countUp));
        System.out.println("Створення файлу шляхом збору знайденої інформації..");
    }
    private void informationParser(double perc) {
        String res = String.format("%.2f", perc);
        System.out.println("Прогрес створення: " + res + "%..");
    }
    public void disableConnection(WebDriver conDriver) {
        conDriver.quit();
    }
    public List<Information> parseEachRequest(String data) throws InterruptedException {
        int num = 1, countUp;

        List<Information> information = new ArrayList<>();

        WebDriver driver = searchRequest(data);

        Document document = Jsoup.parse(driver.getPageSource());

        Elements elem = document.select("div.goods-tile");

        if (!elem.isEmpty()) {
            countUp = countUp(driver);

            outputInformationToConsole(countUp);

            information = writeDataIntoList(elem, information, num, data);

            informationParser(countUp == 0 ? 100 : generatedPerc(countUp));

            if (countUp > 0) {
                information = writeInfoFromPages(information, driver, num, data, countUp);
            }
        } else {
            Information info = new Information();

            info.setSearch(data);

            info.setDesc("Нічого не було знайдено. Спробуйте ще раз.");

            information.add(info);
        }

        return information;
    }
}