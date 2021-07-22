package apitest;

import java.util.List;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.validator.routines.EmailValidator;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import util.TestBase;
import util.TestUtils;

import static io.restassured.RestAssured.given;
import static util.TestUtils.assertReturnedUserDetails;

public class Mobiquity extends TestBase{

	String userID;
	String userName = "Delphine";
	Response userPosts;
	Response comments;


	public Response users(String endpoint, String queryParam, String queryParamValue) {
		RestAssured.baseURI = baseUrl;
		

		String endpointInfo = "<b>ENDPOINT</b>";
		Markup e = MarkupHelper.createLabel(endpointInfo, ExtentColor.BLUE);
		testInfo.get().info(e);
		testInfo.get().info(baseUrl + endpoint);
		
		String bodyInfo = "<b>Request Body</b>";
		Markup b = MarkupHelper.createLabel(bodyInfo, ExtentColor.BLUE);
		testInfo.get().info(b);
		testInfo.get().info("{\""+queryParam+"\":\""+queryParamValue+"\"}");
			
		Response res =	given().queryParam(queryParam, queryParamValue).when().get(baseUrl + endpoint).then().assertThat().statusCode(200).
				
				extract().response();

		String responseBodyInfo = "<b>Response Body</b>";
		Markup r = MarkupHelper.createLabel(responseBodyInfo, ExtentColor.BLUE);
		testInfo.get().info(r);
		String response = res.asString();
		testInfo.get().info(MarkupHelper.createCodeBlock(response));
		return res;

	}

	@Test
	public void getUser() {

		TestUtils.testTitle("Confirm that all user details are returned for the user with username ("+userName+")");
		Response res = users("users","username", userName);
		String response = res.asString();
		JsonPath jsonRes = new JsonPath(response);
		userID = jsonRes.getString("id").replaceAll("\\[|\\]", "");;
		assertReturnedUserDetails(jsonRes);
	}

	@Test
	public void getUserPosts() {
		TestUtils.testTitle("Get user details for user with username ("+userName+")");
		Response res = users("users","username", userName);
		String response = res.asString();
		JsonPath jsonRes = new JsonPath(response);
		userID = jsonRes.getString("id").replaceAll("\\[|\\]", "");

	}

	@Test
	public void validateUserPostComments() {
		TestUtils.testTitle("Get all posts by user with username (" +userName+ ")");
		userPosts  = users("posts","userId", userID);
		TestUtils.testTitle("Get all  comments for the posts by user (" + userName + ") and validate the email address of the commenter's");
		List<Integer> postResponse = userPosts.jsonPath().getList("id");
		for (int i = 0; i < postResponse.size(); i++) {
			int postId = postResponse.get(i);
			comments = users("comments", "postId", String.valueOf(postId));
			List<String> commentEmails = comments.jsonPath().getList("email");
			for (int j = 0; j < commentEmails.size(); j++) {
				String email = commentEmails.get(j);

				TestUtils.testTitle("Confirm that Comment by user with email address("+email+") for Post with post id ("+postId+") is not empty");
				try{
					Assert.assertNotEquals(email, "");
					testInfo.get().info("Email address ("+email+") is not empty");
				}catch(Exception e){
					testInfo.get().error("Email address ("+email+") is empty");
				}

				TestUtils.testTitle("Confirm that comment email address ("+email+") is valid");
				if( EmailValidator.getInstance(true).isValid(email)){
					testInfo.get().info("Email address ("+email+") is valid");
				}else{
					testInfo.get().info("Email address ("+email+") is not valid");
				}
			}
		}
	}

}
