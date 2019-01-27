package com.cermati.imams.githubsearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApi {

    String BASE_URL = "https://api.github.com";

    @GET("/search/users")
    Call<SearchResponse> getResults(@Query("q") String query, @Query("page") int page);
}
