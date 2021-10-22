package tests;

import com.github.javafaker.Faker;
import data.Category;
import data.Product;
import db.dao.CategoriesMapper;
import db.dao.ProductsMapper;
import enums.CategoryType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import service.CategoryService;
import service.ProductService;
import utilities.DBUtils;
import utilities.PrettyLogger;
import utilities.RetrofitUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ProductTests {
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    static CategoriesMapper categoriesMapper;
    Faker faker = new Faker();
    Product product;
    Product product2;
    PrettyLogger prettyLogger = new PrettyLogger();
    static ProductsMapper productsMapper;


    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);
        productsMapper = DBUtils.getProductsMapper();
        categoriesMapper = DBUtils.getCategoriesMapper();
    }

    @BeforeEach
    void fullInfo() {
        product = new Product()
                .withTitle(faker.food().spice())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryType.FOOD.getTitle());
    }

    @BeforeEach
    void noInfo() {
        product2 = new Product();
    }

    @Test
    Integer postProductTest() throws IOException {
        Integer countProductBefore = DBUtils.countProducts(productsMapper);
        Response<Product> response = productService.createProduct(product).execute();
        Integer countProductAfter = DBUtils.countProducts(productsMapper);
        prettyLogger.log(response.body().toString());
        assertThat(countProductAfter, equalTo(countProductBefore + 1));
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        return response.body().getId();
    }

    @Test
    void postNoInfoProductTest() throws IOException {
        Integer countProducts = DBUtils.countProducts(productsMapper);
        Response<Product> response = productService.createProduct(product2).execute();
        Integer countProductAfter = DBUtils.countProducts(productsMapper);
        assertThat(countProductAfter, equalTo(countProducts));
        assertThat(response.body(), equalTo(null));
        assertThat(response.isSuccessful(), is(false));
    }

    @Test
    void getCategoryByIdTest() throws IOException {
        Integer countCategories = DBUtils.countCategories(categoriesMapper);
        DBUtils.createNewCategory(categoriesMapper);
        Integer id = CategoryType.FOOD.getId();
        Response<Category> response = categoryService
                .getCategory(id)
                .execute();
        prettyLogger.log(response.body().toString());
        Integer countCategoriesAfter = DBUtils.countCategories(categoriesMapper);
        assertThat(countCategoriesAfter, equalTo(countCategories + 1));
        assertThat(response.body().getId(), equalTo(id));
        assertThat(response.body().getTitle(), equalTo(CategoryType.FOOD.getTitle()));
    }

    @Test
    void getProductByIdTest() throws IOException {
        Integer id = postProductTest();
        Response<Product> response = productService
                .getProduct(id)
                .execute();
        prettyLogger.log(response.body().toString());
        assertThat(response.body().getId(), equalTo(id));
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
    }


    @Test
    void getProductByNullIdTest() throws IOException {
        Integer id = 0;
        Response<Product> response = productService
                .getProduct(id)
                .execute();
        assertThat(response.body(), equalTo(null));
        assertThat(response.isSuccessful(), is(false));
    }

    @Test
    void getProductByNonExistIdTest() throws IOException {
        Integer id = (int) ((Math.random() + 1) * 1000000);
        Response<Product> response = productService
                .getProduct(id)
                .execute();
        assertThat(response.body(), equalTo(null));
        assertThat(response.isSuccessful(), is(false));
    }

    @Test
    void updateInfoTest() throws IOException {
        Integer id = postProductTest();
        Product change = new Product()
                .withId(id)
                .withTitle(faker.food().spice())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryType.FOOD.getTitle());
        Response<Product> response = productService.updateProduct(change).execute();
        prettyLogger.log(response.body().toString());
        assertThat(response.body().getTitle(), equalTo(change.getTitle()));
        assertThat(response.body().getCategoryTitle(), equalTo(change.getCategoryTitle()));
        assertThat(response.body().getPrice(), equalTo(change.getPrice()));
    }

    @Test
    void updatePassIdOnlyTest() throws IOException {
        Integer id = postProductTest();
        Product change = new Product()
                .withId(id);
        Response<Product> response = productService.updateProduct(change).execute();
        assertThat(response.isSuccessful(), is(false));
    }

    @Test
    void deleteProductTest() throws IOException {
        Integer countProductBefore = DBUtils.countProducts(productsMapper);
        Integer id = postProductTest();
        Response<ResponseBody> response = productService
                .deleteProduct(id)
                .execute();
        prettyLogger.log(response.body().toString());
        Integer countProductAfter = DBUtils.countProducts(productsMapper);
        assertThat(countProductAfter, equalTo(countProductBefore - 1));
        assertThat(response.isSuccessful(), is(true));
    }

    @Test
    void deleteNonExistProductTest() throws IOException {
        Integer countProductBefore = DBUtils.countProducts(productsMapper);
        Integer id = (int) ((Math.random() + 1) * 1000000);
        Response<ResponseBody> response = productService
                .deleteProduct(id)
                .execute();
        Integer countProductAfter = DBUtils.countProducts(productsMapper);
        assertThat(countProductAfter, equalTo(countProductBefore));
        assertThat(response.isSuccessful(), is(false));
    }

    @Test
    void deleteNullProductTest() throws IOException {
        Integer countProductBefore = DBUtils.countProducts(productsMapper);
        Integer id = 0;
        Response<ResponseBody> response = productService
                .deleteProduct(id)
                .execute();
        Integer countProductAfter = DBUtils.countProducts(productsMapper);
        assertThat(countProductAfter, equalTo(countProductBefore));
        assertThat(response.isSuccessful(), is(false));
    }
}
