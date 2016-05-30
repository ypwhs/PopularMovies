package com.yangpeiwen.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.yangpeiwen.popularmovies.R.id.recyclerView;

public class MainActivity extends AppCompatActivity {
    private MyAdapter mAdapter;
    private ExecutorService pool = Executors.newFixedThreadPool(2);
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(recyclerView);
        assert mRecyclerView != null;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setItemViewCacheSize(100);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                getPopularMovies(1);
            }
        });


    }

    private String httpGET(String url) {
        String responseString = "";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseString;
    }

    Lock popularMoviesLock = new ReentrantLock();
    private int lastPage = 0;
    private void getPopularMovies(int page) {
        if(lastPage == page) return;
        lastPage = page;
        if (mAdapter.resultsBeen.length > 0 && mAdapter.resultsBeen[(page - 1) * 20] != null) {
            Log.d("已获取", "page:" + page);
            return;
        }
        Log.d("getPopularMovies", "获取电影数据:" + page);
        String key = getString(R.string.key);
        String url = "http://api.themoviedb.org/3/movie/popular?language=zh&api_key=" +
                key +
                "&page=" +
                page;
        String response;
        response = httpGET(url);
        Log.v("url", url);

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
            runOnUiThread(refreshListView);
        } else {
            final Activity activity = this;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "获取失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Runnable refreshListView = new Runnable() {
        @Override
        public void run() {
            mAdapter.notifyDataSetChanged();
        }
    };

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.CustomViewHolder> {
        PopularJSON.ResultsBean resultsBeen[] = new PopularJSON.ResultsBean[0];
        Context context;

        MyAdapter(Context c) {
            context = c;
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;

            CustomViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.postImageView);
                textView = (TextView) itemView.findViewById(R.id.movieNameTextView);
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.post_image, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, final int position) {
            PopularJSON.ResultsBean currentResult = resultsBeen[position];
            if (currentResult != null) {
                holder.textView.setText(currentResult.getTitle());
                String postPrefix = "https://image.tmdb.org/t/p/w370";
                Picasso.with(context)
                        .load(postPrefix + currentResult.getPoster_path())
                        .into(holder.imageView);
            } else {
                final int page = position / 20 + 1;
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        getPopularMovies(page);
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
