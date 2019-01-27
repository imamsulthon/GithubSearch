package com.cermati.imams.githubsearch.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cermati.imams.githubsearch.R;
import com.cermati.imams.githubsearch.model.User;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserDetailsActivity extends AppCompatActivity {

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_url)
    TextView tvUrl;
    @BindView(R.id.tv_html_url)
    TextView tvHtmlUrl;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        ButterKnife.bind(this);

        user = getIntent().getParcelableExtra("user");

        Picasso.with(this)
                .load(user.getAvatarUrl())
                .centerCrop()
                .fit()
                .into(ivAvatar);

        tvUsername.setText(user.getUsername());
        tvHtmlUrl.setText(user.getHtmlUrl());
        tvUrl.setText(user.getUrl());
    }
}
