package com.yangpeiwen.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailActivity extends AppCompatActivity {
    private MyAdapter mAdapter;
    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if (i.getSerializableExtra("data") == null) finish();
        PopularJSON.ResultsBean data = (PopularJSON.ResultsBean) i.getSerializableExtra("data");
        Log.d("data", data.getOverview());

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //设置工具栏背景图为海报图
        ImageView toolbarImageView = (ImageView) findViewById(R.id.toolbarImageView);
        String postPrefix = "https://image.tmdb.org/t/p/w370";
        Picasso.with(this)
                .load(postPrefix + data.getPoster_path())
                .into(toolbarImageView);

        //标题电影名称
        setTitle(data.getTitle());

        //左上角返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //片名 有中文则显示中文
        TextView movieNameTextView2 = (TextView) findViewById(R.id.movieNameTextView2);
        if (movieNameTextView2 != null) {
            String title = data.getTitle();
            String originalTitle = data.getOriginal_title();
            if (!title.equals(originalTitle)) title += "\n" + originalTitle;
            movieNameTextView2.setText(title);
        }

        //上映日期 评分
        TextView releaseTextView = (TextView) findViewById(R.id.releaseTextView);
        if (releaseTextView != null)
            releaseTextView.setText(String.format(Locale.CHINA, "上映日期:%s\n平均评分:%.2f", data.getRelease_date(), data.getVote_average()));

        //评分星星
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        if (ratingBar != null)
            ratingBar.setRating((float) data.getVote_average() / 2);

        //剧情简介
        TextView overviewTextView = (TextView) findViewById(R.id.overviewTextView);
        if (overviewTextView != null)
            overviewTextView.setText(data.getOverview());

        //剧照
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.imageRecyclerView);
        if (mRecyclerView == null) return;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemViewCacheSize(20);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        final int movieID = data.getId();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                getMovieImages(movieID);
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.CustomViewHolder> {
        List<MovieImagesJSON.BackdropsBean> bean = new ArrayList<>();
        Context context;

        MyAdapter(Context c) {
            context = c;
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView backdropsImageView;

            CustomViewHolder(View itemView) {
                super(itemView);
                backdropsImageView = (ImageView) itemView.findViewById(R.id.backdropsImageView);
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.image_backdrops, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, final int position) {
            String postPrefix = "https://image.tmdb.org/t/p/w370";
            Picasso.with(context)
                    .load(postPrefix + bean.get(position).getFile_path())
                    .into(holder.backdropsImageView);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return bean.size();
        }

    }

    private Common common = new Common();
    private ExecutorService pool = Executors.newFixedThreadPool(1);

    private void getMovieImages(final int id) {
        Log.d("getMovieImages", "电影id:" + id);
        String key = getString(R.string.key);
        String url = "http://api.themoviedb.org/3/movie/" + id + "/images?api_key=" + key;
        String response;
        Log.v("url", url);
        response = common.httpGET(url);

        Gson gson = new Gson();
        MovieImagesJSON json = gson.fromJson(response, MovieImagesJSON.class);
        if (json != null) {
            mAdapter.bean = json.getBackdrops();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View recyclerView = findViewById(R.id.imageRecyclerView);
                    if (recyclerView == null) return;
                    mAdapter.notifyDataSetChanged();
                }
            });
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View recyclerView = findViewById(R.id.imageRecyclerView);
                    if (recyclerView == null) return;
                    Snackbar snackbar = Snackbar.make(recyclerView, "获取剧照失败", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            });
        }
    }

}
