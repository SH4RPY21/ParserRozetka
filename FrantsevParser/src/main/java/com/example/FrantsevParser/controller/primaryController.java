package com.example.FrantsevParser.controller;

import com.example.FrantsevParser.model.Information;
import com.example.FrantsevParser.services.ExcelFileGeneration;
import com.example.FrantsevParser.services.Parser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class primaryController {
    @GetMapping("/")
    public String main() {
        return "main";
    }
    @GetMapping("/main")
    public String mainIndex() {
        return "main";
    }
    @GetMapping("/desc")
    public String desc(Model model) {
        model.addAttribute("descName", "Парсер зробив: Францев Богдан Вікторович, група 122-20-ск1");
        model.addAttribute("descText", "Як користуватись:перейдіть ан сторінку парсеру за допомогою меню введіть назву товару в полі пошуку та натисніть шукати товар.");
        return "desc";
    }
    @GetMapping("/parser")
    public String parser(Model model) {
        model.addAttribute("parserDesc", "Програма-парсер для сайту Розетка");
        model.addAttribute("parserNotification", "Увага! Пошук товару може займати деякий час.");
        return "parser";
    }
    @PostMapping("/injection")
    public ResponseEntity<ByteArrayResource> process(@RequestParam String name) throws InterruptedException, IOException {
        List<Information> list = new ArrayList<>();

        DateFormat format = new SimpleDateFormat("MM_dd_yyy_hh_mm_ss_a");

        String date = format.format(Calendar.getInstance().getTime());

        String fileName = "product_name_" + date;

        if (name.isEmpty()){
            fileName = "empty_query_" + date;

            Information emp = new Information();

            emp.setSearch("empty_query_");

            emp.setDesc("empty_query_");

            list.add(emp);
        }

        else {
            ChromeOptions options = new ChromeOptions();

            options.addArguments("--headless");

            options.addArguments("--remote-allow-origins=*");

            WebDriver driver = new ChromeDriver(options);

            Parser parser = new Parser();

            parser.enableConnection(driver);

            list = parser.parseEachRequest(name);

            parser.disableConnection(driver);
        }

        if (list.size() != 0) {
            ExcelFileGeneration excelGen = new ExcelFileGeneration();

            List<String> column = new ArrayList<>();

            column.add("№ сторінки");

            column.add("Назва товару");

            column.add("Номер товару");

            column.add("Опис товару");

            column.add("Ціна товару");

            column.add("Наявність товару");

            column.add("Посилання на товар");

            excelGen.createColCaps(column);
            excelGen.createExcelFile(fileName, list);
        }

        String path = "./excelFiles/" + fileName + ".xls";

        Path getPath = Paths.get(path);

        byte[] data = Files.readAllBytes(getPath);

        Thread.sleep(2000);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + getPath.getFileName().toString())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }
}