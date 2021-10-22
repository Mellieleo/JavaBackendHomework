package service;

import data.Category;
import retrofit2.*;
import retrofit2.http.*;

public interface CategoryService {
    @GET("categories/{id}")
    Call<Category> getCategory(@Path("id") Integer id);
}
