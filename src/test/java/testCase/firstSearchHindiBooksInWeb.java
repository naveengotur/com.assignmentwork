package testCase;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

public class firstSearchHindiBooksInWeb extends BaseClassTNG {

	@Test
	public void searchHindiBooks() throws Exception {

		driver.manage().window().maximize();

		// to open the Ecommerce Website page
		// driver.get("https://www.sapnaonline.com/");

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

	}
}
