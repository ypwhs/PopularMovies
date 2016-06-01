package com.yangpeiwen.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private MyAdapter mAdapter;
    private ExecutorService pool = Executors.newFixedThreadPool(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        if (mRecyclerView == null) return;
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                Context context = mAdapter.context;
//                Picasso picasso = Picasso.with(context);
//                if (newState == SCROLL_STATE_SETTLING || newState == SCROLL_STATE_DRAGGING) {
//                    picasso.pauseTag("main");
//                } else if (newState == SCROLL_STATE_IDLE) {
//                    picasso.resumeTag("main");
//                }
//            }
//        });
//        pool.execute(new Runnable() {
//            @Override
//            public void run() {
//                getMovies(1);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private String order = "";
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String o = sharedPreferences.getString("order", "1");
        String order2 = o.equals("1") ? "popular" : "top_rated";
        if(!order.equals(order2)){
            order = order2;
            refreshList();
        }
        Log.d("order", order);
    }

    private void refreshList(){
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                getMovies(1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("home", "home");
                return true;
            case R.id.menu_refresh:
                refreshList();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private final Activity activity = this;
    private Common common = new Common();

    private void getMovies(final int page) {
        if (mAdapter.resultsBeen.length > 0 && mAdapter.resultsBeen[(page - 1) * 20] != null) {
            Log.d("已获取", "page:" + page);
            return;
        }
        Log.d("getMovies", "获取电影数据:" + page);
        String key = getString(R.string.key);
        String url = "http://api.themoviedb.org/3/movie/" + order + "?language=zh&api_key=" +
                key +
                "&page=" +
                page;
        Log.v("url", url);
        String response;
        response = common.httpGET(url);


        Gson gson = new Gson();
        PopularJSON popular = gson.fromJson(response, PopularJSON.class);
        if (popular != null) {
            if (mAdapter.resultsBeen.length == 0) {
                int num = popular.getTotal_results();
                Log.d("电影总数", "" + num);
                mAdapter.resultsBeen = new PopularJSON.ResultsBean[num];
            }
            List<PopularJSON.ResultsBean> results = popular.getResults();
            System.arraycopy(results.toArray(), 0, mAdapter.resultsBeen, (page - 1) * 20, results.size());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                    if (mRecyclerView != null) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View recyclerView = findViewById(R.id.recyclerView);
                    if (recyclerView == null) return;
                    Snackbar snackbar = Snackbar.make(recyclerView, "获取失败", Snackbar.LENGTH_LONG);
                    snackbar.setAction("重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    getMovies(page);
                                }
                            });
                        }
                    });
                    snackbar.show();
                }
            });
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.CustomViewHolder> {
        PopularJSON.ResultsBean resultsBeen[] = new PopularJSON.ResultsBean[0];
        Context context;
        Picasso mPicasso;

        MyAdapter(Context c) {
            context = c;
            mPicasso = Picasso.with(context);
//            mPicasso.setIndicatorsEnabled(true);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView postImageView;
            TextView movieNameTextView;
            View view;

            CustomViewHolder(View itemView) {
                super(itemView);
                postImageView = (ImageView) itemView.findViewById(R.id.postImageView);
                movieNameTextView = (TextView) itemView.findViewById(R.id.movieNameTextView);
                view = itemView;
            }
        }

        void clear() {
            resultsBeen = new PopularJSON.ResultsBean[0];
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.image_post, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            final PopularJSON.ResultsBean bean = resultsBeen[position];
            if (bean != null) {
                holder.movieNameTextView.setText(bean.getTitle());
                final String postPrefix = "https://image.tmdb.org/t/p/w342";
                ImageView imageView = holder.postImageView;
                mPicasso.load(postPrefix + bean.getPoster_path())
                        .into(imageView);

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, DetailActivity.class);
                        intent.putExtra("data", bean);
                        startActivity(intent);
                    }
                };
                holder.view.setOnClickListener(clickListener);
                holder.postImageView.setOnClickListener(clickListener);
            } else {
                final int page = position / 20 + 1;
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        getMovies(page);
                    }
                });
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return resultsBeen.length;
        }

    }

}
