package com.cermati.imams.githubsearch.activity;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.cermati.imams.githubsearch.adapter.EndlessRecyclerViewOnScrollListener;
import com.cermati.imams.githubsearch.service.NetworkUtility;
import com.cermati.imams.githubsearch.R;
import com.cermati.imams.githubsearch.service.RetrofitApi;
import com.cermati.imams.githubsearch.model.SearchResponse;
import com.cermati.imams.githubsearch.model.User;
import com.cermati.imams.githubsearch.adapter.UserRecyclerViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.loading_indicator)
    ProgressBar progressBar;
    @BindView(R.id.search_view)
    FloatingSearchView searchView;

    private ArrayList<User> userArrayList = new ArrayList<>();
    private Context context;
    private UserRecyclerViewAdapter adapter;

    private LinearLayoutManager layoutManager;
    int pageIndex, totalPages;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.LEFT);
            getWindow().setExitTransition(slide);
        }
        context = getApplicationContext();

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);;
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new UserRecyclerViewAdapter(context, userArrayList);
        recyclerView.setAdapter(adapter);

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
            }

            @Override
            public void onSearchAction(String currentQuery) {
                recyclerView.smoothScrollToPosition(0);
                getResult(currentQuery);
                pageIndex = 2;
                query = currentQuery;
            }
        });

        recyclerView.addOnScrollListener(new EndlessRecyclerViewOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMore(query);
            }
        });
    }

    private void getResult(String query) {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        RetrofitApi retrofitApi = NetworkUtility.getCacheEnabledRetrofit(getApplicationContext()).create(RetrofitApi.class);
        Call<SearchResponse> call = retrofitApi.getResults(query, 1);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                userArrayList.clear();
                if (searchResponse != null) {
                    userArrayList.addAll(searchResponse.getUserArrayList());
                    adapter.notifyDataSetChanged();

                    // getting total results and number of pages posibility
                    int totalResults = searchResponse.getTotalCount();
                    int pages = totalResults/30;
                    totalPages = pages;
                    if ((totalResults % 30) > 0) {
                        totalPages = totalPages + 1;
                    }
                    Toast.makeText(context, "Total results: " + totalResults +
                            ", Total pages = " + totalPages, Toast.LENGTH_LONG).show();
                }
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
                recyclerView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadMore(String query) {
        if (pageIndex <= totalPages) {
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Pages: " + pageIndex, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RetrofitApi retrofitApi = NetworkUtility.getCacheEnabledRetrofit(getApplicationContext()).create(RetrofitApi.class);
                    Call<SearchResponse> call = retrofitApi.getResults(query, pageIndex);
                    call.enqueue(new Callback<SearchResponse>() {
                        @Override
                        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                            SearchResponse searchResponse = response.body();
                            if (searchResponse != null) {
                                userArrayList.addAll(searchResponse.getUserArrayList());
                                adapter.notifyDataSetChanged();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                        @Override
                        public void onFailure(Call<SearchResponse> call, Throwable t) {
                            Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
                            recyclerView.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    });
                }
            }, 1500);
            pageIndex++;
        }
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchBarFocused()) {
            searchView.clearQuery();
            pageIndex = 2;
        } else {
            super.onBackPressed();
        }
    }
}
