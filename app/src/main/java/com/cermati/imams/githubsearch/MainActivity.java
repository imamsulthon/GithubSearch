package com.cermati.imams.githubsearch;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements UserRecyclerViewAdapter.ItemClickListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.loading_indicator)
    ProgressBar progressBar;
    @BindView(R.id.search_view)
    FloatingSearchView searchView;

    private ArrayList<User> userArrayList = new ArrayList<>();
    private Context context;
    private UserRecyclerViewAdapter adapter;

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);;
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new UserRecyclerViewAdapter(context, userArrayList, this);
        recyclerView.setAdapter(adapter);

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                recyclerView.smoothScrollToPosition(0);
                getResult(currentQuery);
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
                    Log.e("Result", searchResponse.getUserArrayList().toString());
                    Toast.makeText(context, "Total page count: " + searchResponse.getTotalCount(), Toast.LENGTH_LONG).show();
                }
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
                recyclerView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClickListener(int position, ImageView avatar) {
//        User user;
//        user = userArrayList.get(position);
//        Intent intent = new Intent(context, UserDetailsActivity.class);
//        intent.putExtra("user", user);
//        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchBarFocused()) {
            searchView.clearQuery();
        } else {
            super.onBackPressed();
        }
    }
}
