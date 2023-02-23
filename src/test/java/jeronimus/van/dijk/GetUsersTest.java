package jeronimus.van.dijk;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetUsersTest {
    Response response;

    @BeforeSuite()
    public void setupTests(){
        RestAssured.baseURI = "https://reqres.in";

        response = given ()
                .contentType(ContentType.JSON)
                .queryParam("page", "1")
                .queryParam("per_page", "2")
                .when()
                .get("/api/users/1")
                .then()
                .extract().response();
    }

    @Test
    public void HTTPStatusCode200() {
        assertThat("HTTP Status is 200", response.statusCode(), equalTo(HTTP_OK));
    }

    @Test
    public void VerifyJsonSchemaFromResponse()
    {
        assertThat("JSON Schema is correct", response.asString(), matchesJsonSchemaInClasspath("jsonSchema.json"));
    }

    @Test
    public void VerifyJsonSchemaFromInline() {
        RestAssured.
                when()
                .get("/api/users/1")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType("application/json; charset=utf-8")
                .header("Server", "cloudflare")
                .body("data.email", equalTo("george.bluth@reqres.in")) // data.email is using Groovy GPath
                .body("size()", CoreMatchers.is(2)) // Number of items in the object
                .body(matchesJsonSchemaInClasspath("jsonSchema.json"));
    }
}
