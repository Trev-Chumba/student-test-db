package com.Compulynx.student_test_db.FileHandling;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Service
public class ExcelService {
    private static final int CHUNK_SIZE = 1000;
    private static final int MIN_SCORE = 50;
    private static final int MAX_SCORE = 85;
    //private static final String SAVE_DIR = "C:/var/log/docs";
    private static final String SAVE_DIR2 = System.getProperty("user.home") + "/Downloads/excel/";
    private static final String DIR = System.getProperty("user.home") + "/var/log/applications/API/dataprocessing/";


    private Map<String, CellStyle> createStyles(Workbook workbook) {
        Map<String, CellStyle> styles = new HashMap<>();

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        styles.put("header", headerStyle);

        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
        styles.put("date", dateStyle);

        return styles;
    }

    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "firstName", "lastName", "DOB", "studentClass", "score", "status", "photoPath"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Freeze header row
        sheet.createFreezePane(0, 1);
    }

    public String generateAndSaveExcel(int students, String fileName, Consumer<Double> progressConsumer) throws IOException {

        File directory = new File(DIR);
        if (!directory.exists()){
            directory.mkdirs();
        }

        String filePath = DIR + File.separator + fileName + ".xlsx";

        SXSSFWorkbook workbook = new SXSSFWorkbook(null, 120);
        workbook.setCompressTempFiles(true);

        try{
            Sheet sheet = workbook.createSheet("Students");

            Map<String, CellStyle> styles = createStyles(workbook);
            createHeaderRow(sheet, styles.get("header"));

//            for(int i =0; i < students; i+=CHUNK_SIZE){
//                int end = Math.min(i + CHUNK_SIZE, students);
//                processStudentChunk(sheet, students, i+1);
//                double progress = (double) (end) / students;
//                progressConsumer.accept(progress);
//
//                if (i % (CHUNK_SIZE * 10) == 0) {
//                    System.gc();
//                }
//            }

            for (int i = 1; i < students; i += CHUNK_SIZE) {

                int end = Math.min(i + CHUNK_SIZE, students);

                // Process the student chunk and pass the correct starting row index
                processStudentChunk(sheet, end, i);

                // Calculate progress
                double progress = (double) (end) / students;
                progressConsumer.accept(progress);


                if (i % CHUNK_SIZE == 0) {
                    System.gc();
                }
            }

            sheet.setColumnWidth(0, 3000);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 5000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 3000);
            sheet.setColumnWidth(5, 3000);
            sheet.setColumnWidth(6, 4000);
            sheet.setColumnWidth(7, 8000);

            try(FileOutputStream fileOutputStream = new FileOutputStream(filePath)){
                workbook.write(fileOutputStream);
            }

            return filePath;
        }finally {
            workbook.dispose();
            workbook.close();
        }
    }

    private void processStudentChunk(Sheet sheet, int numRows, int startRowNum)
    {

        int rowNum = startRowNum;

        Map<String, CellStyle> styles = createStyles(sheet.getWorkbook());


        for( int i = rowNum; i < numRows; i++)
        {
            LocalDate start = LocalDate.of(2000, Month.JANUARY, 1);
            LocalDate end = LocalDate.of(2010, Month.DECEMBER, 31);
            long randomDay = ThreadLocalRandom.current().nextLong(start.toEpochDay(), end.toEpochDay() + 1);
            LocalDate randomDate = LocalDate.ofEpochDay(randomDay);

            String [] classes = {"Class1", "Class2", "Class3", "Class4", "Class5"};
            Random randomClass = new Random();
            int randomIndex = randomClass.nextInt(classes.length);



            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(RandomStringUtils.randomAlphabetic(8));
            row.createCell(2).setCellValue(RandomStringUtils.randomAlphabetic(8));
            row.createCell(3).setCellValue(java.sql.Date.valueOf(randomDate));
            row.getCell(3).setCellStyle(styles.get("date"));
            row.createCell(4).setCellValue(classes[randomIndex]);
            row.createCell(5).setCellValue(ThreadLocalRandom.current().nextInt(MIN_SCORE, MAX_SCORE));
            row.createCell(6).setCellValue(ThreadLocalRandom.current().nextInt(0,2));
            row.createCell(7).setCellValue("https://avatar.iran.liara.run/public");
        }
    }

    public String generateExcel(int userRequest, SseEmitter emitter, String fileName)
    {
        System.out.println("generateExcel() STARTED for: " + fileName);
        try{
            return generateAndSaveExcel(userRequest, fileName, progress ->{
                try{
                    emitter.send(SseEmitter.event()
                            .name("progress")
                            .data(String.format("%.2f", progress * 100)));
                }catch (IOException e){ e.printStackTrace();}
            });
        }catch (IOException e){
            throw  new RuntimeException("Failed to generate Excel: " + e.getMessage(), e);
        }finally {
            emitter.complete();
        }
    }

}
