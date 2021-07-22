package util;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;



public class TestBase {
	public static ExtentReports reports;
	public static ExtentHtmlReporter htmlReporter;
	private static ThreadLocal<ExtentTest> parentTest = new ThreadLocal<ExtentTest>();
	public static ThreadLocal<ExtentTest> testInfo = new ThreadLocal<ExtentTest>();

	public static String baseUrl = System.getProperty("instance-url", "https://jsonplaceholder.typicode.com/");
	
	@BeforeSuite
	public void beforeSuite() {
		htmlReporter = new ExtentHtmlReporter(new File(System.getProperty("user.dir") + "/ApiTestReport.html"));
		reports = new ExtentReports();
		reports.setSystemInfo("STAGING", baseUrl);
		reports.attachReporter(htmlReporter);
	}
	
	@BeforeClass
    public void beforeClass() {
		ExtentTest parent = reports.createTest(getClass().getName());
		parentTest.set(parent);
    }

	@BeforeMethod(description = "fetch test cases name")
	public void register(Method method) throws InterruptedException {
		ExtentTest child = parentTest.get().createNode(method.getName());
		testInfo.set(child);
	}

	@AfterMethod(description = "to display the result after each test method")
	public void captureStatus(ITestResult result) throws IOException {
		for (String group : result.getMethod().getGroups())
			testInfo.get().assignCategory(group);
		if (result.getStatus() == ITestResult.FAILURE) {
			testInfo.get().fail(result.getThrowable());
		}
		else if (result.getStatus() == ITestResult.SKIP)
			testInfo.get().skip(result.getThrowable());
		else
			testInfo.get().pass(result.getName() +" Test passed");

		reports.flush();
	}


}