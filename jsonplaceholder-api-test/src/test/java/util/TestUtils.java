package util;

import java.util.HashMap;
import java.util.Map;

import com.aventstack.extentreports.Status;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class TestUtils extends TestBase{

	public static void assertReturnedUserDetails(JsonPath jsonRes) {
		String id = jsonRes.getString("id").replaceAll("\\[|\\]", "");
		String name = jsonRes.getString("name").replaceAll("\\[|\\]", "");
		String username = jsonRes.getString("username").replaceAll("\\[|\\]", "");
		String email = jsonRes.getString("email").replaceAll("\\[|\\]", "");
		String address = jsonRes.getString("address").replaceAll("\\[|\\]", "");
		String phone = jsonRes.getString("phone").replaceAll("\\[|\\]", "");
		String website = jsonRes.getString("website").replaceAll("\\[|\\]", "");
		String company = jsonRes.getString("company").replaceAll("\\[|\\]", "");
		try {
			String empty = "";
			Map<String, String> fields = new HashMap<>();
			fields.put("id", id);
			fields.put("name", name);
			fields.put("username", username);
			fields.put("email", email);
			fields.put("address", address);
			fields.put("phone", phone);
			fields.put("website", website);
			fields.put("company", company);

			for (Map.Entry<String, String> entry : fields.entrySet()) {
				try {
					Assert.assertNotEquals(entry.getValue(), empty);
					Assert.assertNotEquals(entry.getValue(), null);
					testInfo.get().log(Status.INFO, "<b>" + entry.getKey() + "</b> : " + entry.getValue());
				} catch (Error e) {
					testInfo.get().error("<b>" + entry.getKey() + "</b> : " + entry.getValue());
				}

			}
		} catch (Exception g) {
			testInfo.get().error(g);
		}

	}

		public static void testTitle(String phrase) {
			String word = "<b>"+phrase+"</b>";
	        Markup w = MarkupHelper.createLabel(word, ExtentColor.BLUE);
	        testInfo.get().info(w);
		}

}