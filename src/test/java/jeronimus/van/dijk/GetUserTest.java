package jeronimus.van.dijk;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetUserTest {

    private static final String REQRES_OPENAPI_YAML = "reqres_openapi.yaml";
    private static final OpenApiValidationFilter OPEN_API_VALIDATION_FILTER = new OpenApiValidationFilter(REQRES_OPENAPI_YAML);;


    @Test
    public void verifyOpenApiFromResponse() {
        Response response;
        RestAssured.baseURI = "https://reqres.in";

        response =
                given ()
                    .filter(OPEN_API_VALIDATION_FILTER)
                    .contentType(ContentType.JSON)
                .when()
                    .get("/api/users/1")
                .then()
                    .extract().response();

        assertThat("HTTP Status is 200", response.statusCode(), equalTo(HTTP_OK));

    }

    @Test
    public void VerifyJsonSchemaFromResponse() {
        Response response;
        RestAssured.baseURI = "https://reqres.in";

        response =
                given ()
                    .contentType(ContentType.JSON)
                .when()
                    .get("/api/users/1")
                .then()
                    .extract().response();

        assertThat("JSON Schema is correct", response.asString(), matchesJsonSchemaInClasspath("json_schema_get_user.json"));
    }

    @Test
    public void VerifyJsonSchemaFromInline() {
        RestAssured.baseURI = "https://reqres.in";

        RestAssured
                .when()
                    .get("/api/users/1")
                .then()
                    .assertThat()
                    .statusCode(200)
                    .contentType("application/json; charset=utf-8")
                    .header("Server", "cloudflare")
                    .body("data.email", equalTo("george.bluth@reqres.in")) // data.email is using Groovy GPath
                    .body("size()", CoreMatchers.is(2)) // Number of items in the object
                    .body(matchesJsonSchemaInClasspath("json_schema_get_user.json"));
    }

    @Test
    public void VerifyOpenAPIFromInline() {
        RestAssured.baseURI = "https://reqres.in";

        RestAssured.
                given()
                    .filter(OPEN_API_VALIDATION_FILTER)
                .when()
                    .get("/api/users/1")
                .then()
                    .assertThat()
                    .statusCode(200)
                    .contentType("application/json; charset=utf-8")
                    .header("Server", "cloudflare")
                    .body("data.email", equalTo("george.bluth@reqres.in")) // data.email is using Groovy GPath
                    .body("size()", CoreMatchers.is(2)) // Number of items in the object
                    .body(matchesJsonSchemaInClasspath("json_schema_get_user.json"));
    }
}
