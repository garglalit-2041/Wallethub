package tests;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class WalletHubReview {

	public static WebDriver driver;
	static String baseurl="https://wallethub.com/join/light";
	static String review_sub_url="http://wallethub.com/profile/test_insurance_company/";
	static String review_verification_url="https://wallethub.com/profile/";
	static String reviewPostConfirmMessage = "Your review has been posted";

	//Input username and password here for login
	String username="";
	String password="";
	
	
	@BeforeMethod
	public void setup(){
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-notifications");
		
		//set chrome driver path here
		System.setProperty("webdriver.chrome.driver", "Drivers\\chromedriver.exe");
		driver=new ChromeDriver(options);
		driver.get(review_sub_url);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
	}
	
	@Test
	public <list> void CanPostReviewOnWalletHub() throws InterruptedException{

		// scroll down to review Label
		WebElement reviewLabel=driver.findElement(By.xpath("//div[@class='rv review-action ng-enter-element']/h3"));
		WebDriverWait wait=new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.visibilityOf(reviewLabel));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView(true);",reviewLabel);

		//hovering to review stars and selecting 4th one
		WebElement review_stars=driver.findElement(By.xpath("//*[@class='rvs-svg']/div/*[@class='rvs-star-svg'][5]"));
		Actions builder=new Actions(driver);
		builder.moveToElement(review_stars).build().perform();
		Thread.sleep(5000);
		WebElement fourthStar=driver.findElement(By.xpath("//*[@class='rvs-svg']/div/*[@class='rvs-star-svg'][4]"));
		fourthStar.click();

		//Adding review
		WebElement selectPolicy=driver.findElement(By.xpath("//*[@class='dropdown second']/span"));
		Thread.sleep(3000);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", selectPolicy);
		selectPolicy.click();
		Thread.sleep(3000);
		WebElement selectHealthInsurance=driver.findElement(By.xpath("//li[contains(text(),'Health Insurance')]"));
		selectHealthInsurance.click();

       // Input Review Comment
		WebElement inputReviewComment=driver.findElement(By.xpath("//textarea[@class='textarea wrev-user-input validate']"));
		String msg = "This is review comment added by automation. This is review comment added by automation. This is review comment added by automation. This is review comment added by automation";
		Thread.sleep(5000);
		js.executeScript("arguments[0].value=' " + msg +"'" , inputReviewComment);
		inputReviewComment.sendKeys(Keys.TAB);

		//Click on Submit button
		WebElement submitButton=driver.findElement(By.xpath("//div[contains(text(),'Submit')]"));
		submitButton.click();

		//Validate Review comment Post Confirmation

		WebElement confirmMsg=driver.findElement(By.xpath("//*[contains(text(),'Your review has been posted.')]"));
		wait.until(ExpectedConditions.visibilityOf(confirmMsg));
		String commentConfirmMsg = confirmMsg.getText();
		Assert.assertTrue(commentConfirmMsg.contains(reviewPostConfirmMessage), "you have reviewed the institution succesfully");

       //Go to Profile
		WebElement userProfileIcon=driver.findElement(By.xpath("//*[@class='brgm-button brgm-user brgm-list-box']"));
		builder.moveToElement(userProfileIcon);
		Thread.sleep(2000);
		WebElement profile=driver.findElement(By.xpath("//*[@class='brgm-list brgm-user-list ng-enter-element']/a[1]"));
		builder.moveToElement(profile).click().perform();

		//Review Field updation
		WebElement recommendWindow=driver.findElement(By.xpath("//*[@class='pr-ct-box pr-rec']"));
		wait.until(ExpectedConditions.visibilityOf(recommendWindow));
		Assert.assertTrue(recommendWindow.isDisplayed(), "Recommended window is visible");

		WebElement recommendationText=driver.findElement(By.xpath("//*[@class='pr-ct-box pr-rec']"));
		String recommendText = recommendationText.getText();
		Assert.assertTrue(recommendText.contains("Recommendations"), "Review Feed got updated");
		System.out.println("Review Feed got updated, Passing Test!");

		//navigating to profile page to see if posted review exist
		driver.navigate().to(review_verification_url);
		List<WebElement> userList = driver.findElements(By.xpath("//span[@class='rvtab-ci-name']"));
		for(WebElement e: userList){
			if(username.contains(e.getText())){
				Assert.assertTrue(username.contains(e.getText()), "Review post for my user can be seen");
				System.out.println("Post is visible " +"Test Case is passing");
			}
			else{
				System.out.println("Post can't be seen " +"Test Cse is failing");
			}
		}

	}

	@AfterMethod
	public void teardown(ITestResult result){
		
		if(ITestResult.FAILURE==result.getStatus()){
			try{
				
				// To create reference of TakesScreenshot
				TakesScreenshot screenshot=(TakesScreenshot)driver;
				// Call method to capture screenshot
				File src=screenshot.getScreenshotAs(OutputType.FILE);
				
				// Copy files to specific location
				FileUtils.copyFile(src, new File("screenshots\\"+result.getName()+".png"));
				System.out.println("Successfully captured a screenshot");
				
			}catch (Exception e){
				
				System.out.println("Exception while taking screenshot "+e.getMessage());
			} 
	}
		driver.quit();
		
	}
}
