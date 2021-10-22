package Homework_3.Tests;

import data.CreateAlbum;
import data.UploadImg;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Base64;

import static data.Endpoints.*;
import static io.restassured.RestAssured.given;

public class ImageTests extends BaseTest {
    private final String IMAGE_PATH_1 = "src/main/resources/1.jpg";
    static String encodedFile;
    UploadImg uploadImg;
    UploadImg uploadImg2;
    CreateAlbum createAlbum;
    String imageID;
    String imageID2;
    String albumID;
    MultiPartSpecification base64MPSpec;
    MultiPartSpecification fileSpec;
    static RequestSpecification requestSpecWithAuthAndBase64;
    static RequestSpecification requestSpecWithAuthAndFile;
    static RequestSpecification requestSpecAlbum;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);

        base64MPSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        fileSpec = new MultiPartSpecBuilder(new File("src/main/resources/2.png"))
                .controlName("image")
                .build();

        requestSpecWithAuthAndBase64 = new RequestSpecBuilder()
                .addRequestSpecification(requestSpecWithAuth)
                .addMultiPart(base64MPSpec)
                .addFormParam("title", "I'm did")
                .addFormParam("type", "jpg")
                .addFormParam("description", "me rn.")
                .build();

        requestSpecWithAuthAndFile = new RequestSpecBuilder()
                .addRequestSpecification(requestSpecWithAuth)
                .addMultiPart(fileSpec)
                .addFormParam("title", "Temmie")
                .addFormParam("type", "png")
                .addFormParam("description", "hOI! im Temmie")
                .build();

        requestSpecAlbum = new RequestSpecBuilder()
                .addRequestSpecification(requestSpecWithAuth)
                .addFormParam("title", "Memes and tests all the way")
                .build();

    }

   @Test
    void imgUploadTest() {
        uploadImg = given(requestSpecWithAuthAndBase64)
                .post(UPLOAD_IMG)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(UploadImg.class);
        imageID = uploadImg.getData().getId();
    }

    @Test
    void imgUploadTest2() {
        uploadImg2 = given(requestSpecWithAuthAndFile)
                .post(UPLOAD_IMG)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(UploadImg.class);
        imageID2 = uploadImg2.getData().getId();
    }

    @Test
    void favouriteImgTest() {
        given()
                .spec(requestSpecWithAuth)
                .when()
                .post(FAV_IMG, imageID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void changeImgInfoTest() {
        given()
                .spec(requestSpecWithAuth)
                .formParam("title", "Me done")
                .formParam("description", "This is an image of me passing this course.")
                .when()
                .post(CHANGE_INFO, imageID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void createAlbumTest() {
        createAlbum = given(requestSpecAlbum)
                .post(CREATE_ALBUM)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(CreateAlbum.class);
        albumID = createAlbum.getData().getId();
    }

    @Test
    void addImgToAlbumTest() {
        given()
                .spec(requestSpecWithAuth)
                .formParam("ids[]", imageID, imageID2)
                .post(ADD_IMG_TO_ALBUM, albumID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void changeAlbumInfoTest() {
        given()
                .spec(requestSpecWithAuth)
                .formParam("title", "Memes all the way")
                .formParam("description", "This album contains a lot of memes. Be prepared.")
                .put(ALBUM_INFO, albumID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void deleteAlbumTest() {
        given()
                .spec(requestSpecWithAuth)
                .when()
                .delete(ALBUM_INFO, albumID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void deleteImgTest() {
        given()
                .spec(requestSpecWithAuth)
                .when()
                .delete(CHANGE_INFO, imageID)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void favouriteDeletedImgTest() {
        given()
                .spec(requestSpecWithAuth)
                .when()
                .post(FAV_IMG, imageID)
                .prettyPeek()
                .then()
                .statusCode(404);
    }

    @Test
    void changeDeletedImgInfoTest() {
        given()
                .spec(requestSpecWithAuth)
                .formParam("title", "Me done")
                .formParam("description", "This is an image of me passing this course.")
                .when()
                .post(CHANGE_INFO, imageID)
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
