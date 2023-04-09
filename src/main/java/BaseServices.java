import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public abstract class BaseServices {
    String key= "33dfbb1e95ec9f9734e77b656db335cc";
    String token= "ATTA5e96e3c95c1d1a311f55e1759dcf6f252a31bb467371997036c510d59f2b6ffbCD022344";

    public RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://api.trello.com")
                .addHeader("Content-Type", "")
                .addQueryParam("key", key)
                .addQueryParam("token", token)
                .build();
    }

    public Response postService(String path, Map queryParams) {
        return RestAssured.given()
                .spec(getRequestSpec())
                .queryParams(queryParams)
                .post(path);
    }

    public Response putService(String path, Map queryParams) {
        return RestAssured.given()
                .spec(getRequestSpec())
                .queryParams(queryParams)
                .put(path);
    }

    public Response deleteService(String path) {
        return RestAssured.given()
                .spec(getRequestSpec())
                .delete(path);
    }

    public Response getService(String path) {
        return RestAssured.given()
                .spec(getRequestSpec())
                .get(path);
    }
}
