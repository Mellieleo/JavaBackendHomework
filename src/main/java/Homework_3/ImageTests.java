package Homework_3;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ImageTests extends BaseTest {
    private final String IMAGE_PATH_1 = "src/main/resources/1.jpg";
    static String encodedFile;
    String imageID;
    String albumID;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
    }

   @Test
    void imgUploadTest() {
        imageID = given()
                .header("Authorization", token)
                .multiPart("image", encodedFile)
                .formParam("title", "done")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }

    @Test
    void favouriteImgTest() {
        given()
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image/{id}/favorite", imageID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void changeImgInfoTest() {
        given()
                .header("Authorization", token)
                .formParam("title", "Me done")
                .formParam("description", "This is an image of me passing this course.")
                .when()
                .post("https://api.imgur.com/3/image/{id}", imageID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void createAlbumTest() {
        albumID = given()
                .header("Authorization", token)
                .formParam("title", "Memes and tests all the way")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/album")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath()
                .getString("data.id");
    }

    @Test
    void addImgToAlbumTest() {
        given()
                .header("Authorization", token)
                .formParam("ids[]", imageID)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/album/{albumHash}/add", albumID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void changeAlbumInfoTest() {
        given()
                .header("Authorization", token)
                .formParam("title", "Memes all the way")
                .formParam("description", "This album contains a lot of memes. Be prepared.")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .put("https://api.imgur.com/3/album/{id}", albumID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void deleteAlbumTest() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/image/{deleteHash}", albumID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void deleteImgTest() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/image/{deleteHash}", imageID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void favouriteDeletedImgTest() {
        given()
                .header("Authorization", token)
                .when()
                .post("https://api.imgur.com/3/image/{id}/favorite", imageID)
                .prettyPeek()
                .then()
                .statusCode(404);
    }

    @Test
    void changeDeletedImgInfoTest() {
        given()
                .header("Authorization", token)
                .formParam("title", "Me done")
                .formParam("description", "This is an image of me passing this course.")
                .when()
                .post("https://api.imgur.com/3/image/{id}", imageID)
                .prettyPeek()
                .then()
                .statusCode(404);
    }

    private byte[] getFileContent() {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(IMAGE_PATH_1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
