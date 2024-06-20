package testCase;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ECommerceBookSearchOCR {

    public static void main(String[] args) {
        // Set up desired capabilities and initialize the Appium driver
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "11.0"); // Replace with your Android version
        caps.setCapability("deviceName", "emulator-5554"); // Replace with your device name
        caps.setCapability("browserName", "Chrome"); // Use the mobile browser

        AppiumDriver<MobileElement> driver = null;
        try {
            driver = new AndroidDriver<>(new URL("http://localhost:4723/wd/hub"), caps);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // Open the e-commerce website
            driver.get("https://www.sapnaonline.com/search?keyword=Hindi%20Books");

    		System.out.println(driver.getCurrentUrl());

    		Thread.sleep(4000);

    		TakesScreenshot screenshot = (TakesScreenshot) driver;

    		File source = screenshot.getScreenshotAs(OutputType.FILE);

    		FileUtils.copyFile(source, new File("./screenshot/new.png"));

    		System.out.println("Screenshot is captured");

    		// to locate the price element and get the Book Name through getText()
    		String bookName = driver.findElement(By.xpath("//h2[.='My Little Books : Hindi Alphabet (Hindi)']")).getText();

    		System.out.println("The Book name is " + bookName);

    		// to locate the price element and get the Book Name through getText()
    		String bookPrice = driver.findElement(By.xpath("(//h3[@class='ProductCard__PrcieText-sc-10n3822-7 hnbQgS'])[1]")).getText();

    		System.out.println("The Book name is " + bookPrice);
    		
            // Initialize Tesseract OCR
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("path/to/tessdata"); // Set the tessdata path

            // Prepare data structure for comparison
            Map<String, String> actualBookData = new HashMap<>();

            for (WebElement bookElement : bookElements) {
                // Take a screenshot of the book element
                File screenshot = bookElement.getScreenshotAs(OutputType.FILE);
                try {
                    // Perform OCR on the screenshot to extract text
                    String text = tesseract.doOCR(screenshot);
                    // Extract book name and price from the text (you may need to adjust this based on the text format)
                    String[] lines = text.split("\n");
                    String bookName = lines[0];
                    String bookPrice = lines[1];
                    actualBookData.put(bookName, bookPrice);
                } catch (TesseractException e) {
                    e.printStackTrace();
                }
            }

            // Compare actual data with expected data (this is just an example)
            Map<String, String> expectedBookData = new HashMap<>();
            expectedBookData.put("Expected Book 1", "10.99");
            expectedBookData.put("Expected Book 2", "15.99");

            // Generate the report
            generateReport(actualBookData, expectedBookData);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private static void generateReport(Map<String, String> actualData, Map<String, String> expectedData) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Book Price Comparison");

        // Create headers
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Book Name");
        headerRow.createCell(1).setCellValue("Expected Price");
        headerRow.createCell(2).setCellValue("Actual Price");
        headerRow.createCell(3).setCellValue("Match");

        int rowNum = 1;
        for (Map.Entry<String, String> entry : expectedData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
            String actualPrice = actualData.getOrDefault(entry.getKey(), "Not Found");
            row.createCell(2).setCellValue(actualPrice);
            row.createCell(3).setCellValue(entry.getValue().equals(actualPrice) ? "Yes" : "No");
        }

        // Write the output to a file
        try (FileOutputStream fileOut = new FileOutputStream("BookPriceComparison.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Closing the workbook
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}