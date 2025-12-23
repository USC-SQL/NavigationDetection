package edu.usc.Utilities;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveFiles {

    public static void SaveXLSX(LoadConfig configs_obj, String subject, long duration, String Type){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");
        // Create arrow at index 0 (the first row)
        Row row = sheet.createRow(0);
        // Create a cell at index 0 (the first cell) in the row
        Cell cell1 = row.createCell(0);
        Cell cell2 = row.createCell(1);
        // Set an integer value for the cell
        cell1.setCellValue("Duration");
        cell2.setCellValue(duration);
        // Write the output to a file
        String filepath = "";
        if(Type.equals("WTree")){
            filepath = configs_obj.getProperties().getProperty("WTree_location") + File.separator + subject + File.separator + "WTree-execTime-" + subject +".xlsx";
        }
        if(Type.equals("Detection")){
            filepath = configs_obj.getProperties().getProperty("Detection_output_location") + File.separator + subject + File.separator + "Detection-execTime-" + subject +".xlsx";
        }
        try (FileOutputStream fileOut = new FileOutputStream(filepath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Close the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Timing of " + Type + " successfully saved.");
    }

}
