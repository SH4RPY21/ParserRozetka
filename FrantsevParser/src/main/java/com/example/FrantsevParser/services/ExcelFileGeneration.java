package com.example.FrantsevParser.services;

import com.example.FrantsevParser.model.Information;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelFileGeneration {
    private HSSFWorkbook workbook;
    private HSSFSheet sheet;
    public ExcelFileGeneration() {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("rozetkaParser");
    }
    public HSSFWorkbook getWorkbook(){
        return workbook;
    }
    public HSSFSheet getSheet(){
        return sheet;
    }
    private void addColCaps(Row row, int numCell, String caps){
        row.createCell(numCell).setCellValue(caps);
    }
    public void createColCaps(List<String> caps) {
        int numCell = 0;

        Row row = getSheet().createRow(0);

        for (String capt : caps) {
            addColCaps(row, numCell, capt);
            numCell += 1;
        }
    }
    private void fillSheet(List<Information> list) {
        int row = 0;
        for (Information dataModel : list) {
            fillFile(sheet, ++row, dataModel);
        }
    }
    private static void fillFile(HSSFSheet sheet, int rowN, Information dataModel) {
        Row row = sheet.createRow(rowN);

        sheet.autoSizeColumn(rowN);

        row.createCell(0).setCellValue(dataModel.getNum());
        sheet.autoSizeColumn(0);

        row.createCell(1).setCellValue(dataModel.getSearch());
        sheet.autoSizeColumn(1);

        row.createCell(2).setCellValue(dataModel.getIntNum());
        sheet.autoSizeColumn(2);

        row.createCell(3).setCellValue(dataModel.getDesc());
        sheet.autoSizeColumn(3);

        row.createCell(4).setCellValue(dataModel.getPrice());
        sheet.autoSizeColumn(4);

        row.createCell(5).setCellValue(dataModel.getAvail());
        sheet.autoSizeColumn(5);

        row.createCell(6).setCellValue(dataModel.getLink());
        sheet.autoSizeColumn(6);
    }
    public void createExcelFile(String name, List<Information> list) {
        File path = new File("./excelFiles");

        if (path.mkdirs()) {
            System.out.println("Папка створена");
        }

        System.out.println(path);

        fillSheet(list);

        try (FileOutputStream out = new FileOutputStream(path + "/" + name + ".xls")) {
            getWorkbook().write(out);
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        System.out.println("Файл створений");
    }
}