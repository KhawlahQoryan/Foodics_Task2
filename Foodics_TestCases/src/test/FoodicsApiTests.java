package test;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;

public class FoodicsApiTests {
    private static final String BASE_URL = "https://pay2.foodics.dev/cp_internal";
    private static String token;

    @BeforeClass
    public void setup() {
        // Base URI setup
        RestAssured.baseURI = BASE_URL;
    }

   //Test Case 1: Verify login with valid credentials.
    
    @Test(priority = 1)
    public void testLoginWithValidCredentials() {
        String email = "merchant@foodics.com";
        String password = "123456";

        // Sending POST request to /login endpoint
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}")
                .post("/login");

        // Validate response status code
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code is 200");

        // Extract token for further use
        token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token should not be null");
    }

  //Test Case 2: Verify /whoami endpoint returns correct user information.
   
    @Test(priority = 2, dependsOnMethods = {"testLoginWithValidCredentials"})
    public void testWhoamiWithValidToken() {
        // Sending GET request to /whoami endpoint with a valid token
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .get("/whoami");

        // Validate response status code
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code is 200");

        // Validate the user information in the response
        String email = response.jsonPath().getString("email");
        Assert.assertEquals(email, "merchant@foodics.com", "Email should match the logged-in user");
    }

     //Test Case 3: Verify /whoami endpoint with an invalid token.
   
    @Test(priority = 3)
    public void testWhoamiWithInvalidToken() {
        String invalidToken = "InvalidToken123";

        // Sending GET request to /whoami endpoint with an invalid token
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + invalidToken)
                .get("/whoami");

        // Validate response status code
        Assert.assertEquals(response.getStatusCode(), 401, "Expected status code is 401");

        // Validate the error message in the response
        String errorMessage = response.jsonPath().getString("message");
        Assert.assertEquals(errorMessage, "Invalid token", "Error message should indicate invalid token");
    }
}