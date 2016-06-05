package com.yangpeiwen.popularmovies;

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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yangpeiwen.popularmovies.json.MovieCredits;
import com.yangpeiwen.popularmovies.json.MovieImages;
import com.yangpeiwen.popularmovies.json.PopularMovies;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DetailActivity extends AppCompatActivity {
    private BackdropsAdapter backdropsAdapter;
    private CreditAdapter creditAdapter;

    private Common common = new Common();
    private PopularMovies.ResultsBean data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if (i.getSerializableExtra("data") == null) finish();
        data = (PopularMovies.ResultsBean) i.getSerializableExtra("data");
        Log.d("data", data.getOverview());

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        //剧照
        RecyclerView backdropsRecyclerView = (RecyclerView) findViewById(R.id.backdropsRecyclerView);
        if (backdropsRecyclerView == null) return;
        backdropsRecyclerView.setHasFixedSize(true);
        backdropsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        backdropsRecyclerView.setItemViewCacheSize(20);
        RecyclerView.LayoutManager backdropsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        backdropsRecyclerView.setLayoutManager(backdropsLayoutManager);
        backdropsAdapter = new BackdropsAdapter(this);
        backdropsRecyclerView.setAdapter(backdropsAdapter);

        //演职人员
        RecyclerView creditRecyclerView = (RecyclerView) findViewById(R.id.creditRecyclerView);
        if (creditRecyclerView == null) return;
        creditRecyclerView.setHasFixedSize(true);
        creditRecyclerView.setItemAnimator(new DefaultItemAnimator());
        creditRecyclerView.setItemViewCacheSize(20);
        RecyclerView.LayoutManager creditLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        creditRecyclerView.setLayoutManager(creditLayoutManager);
        creditAdapter = new CreditAdapter(this);
        creditRecyclerView.setAdapter(creditAdapter);

        final int movieID = data.getId();
        run(new Runnable() {
            @Override
            public void run() {
                getMovieImages(movieID);
            }
        });
        run(new Runnable() {
            @Override
            public void run() {
                getMovieCredits(movieID);
            }
        });
        run(new Runnable() {
            @Override
            public void run() {
                getMovieVideos(movieID);
            }
        });

    }

    private void run(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(false);
        thread.start();
    }

    private void dismiss(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private void dismiss(View v, int id) {
        View view = v.findViewById(id);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private void getMovieImages(final int id) {
        Log.d("getMovieImages", "电影id:" + id);
        String key = getString(R.string.key);
        String url = "http://api.themoviedb.org/3/movie/" + id + "/images?api_key=" + key;
        String response;
        Log.v("url", url);
        response = common.httpGET(url);

        Gson gson = new Gson();
        final MovieImages json = gson.fromJson(response, MovieImages.class);
        if (json != null) {
            backdropsAdapter.bean = json.getBackdrops();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //设置工具栏背景图为海报图
                    ImageView toolbarImageView = (ImageView) findViewById(R.id.toolbarImageView);
                    List<MovieImages.BackdropsBean> data = json.getBackdrops();
                    String path = "https://image.tmdb.org/t/p/w370" + data.get(new Random().nextInt(data.size())).getFile_path();
                    Picasso.with(backdropsAdapter.context)
                            .load(path)
                            .into(toolbarImageView);
                    backdropsAdapter.notifyDataSetChanged();
                    dismiss(R.id.backdropsProgressBar);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View recyclerView = findViewById(R.id.backdropsRecyclerView);
                    if (recyclerView == null) return;
                    Snackbar snackbar = Snackbar.make(recyclerView, "获取剧照失败", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    dismiss(R.id.backdropsProgressBar);
                }
            });
        }
    }

    private void getMovieCredits(final int id) {
        Log.d("getMovieCredits", "电影id:" + id);
        String key = getString(R.string.key);
        String url = "http://api.themoviedb.org/3/movie/" + id + "/credits?api_key=" + key;
        String response;
        Log.v("url", url);
        response = common.httpGET(url);

        Gson gson = new Gson();
        MovieCredits json = gson.fromJson(response, MovieCredits.class);
        if (json != null) {
            final List<MovieCredits.CastBean> castBeen = json.getCast();
            final List<MovieCredits.CrewBean> crewBeen = json.getCrew();

            for (int i = 0; i < crewBeen.size(); i++) {
                MovieCredits.CrewBean crew = crewBeen.get(i);
                if (crew.getProfile_path() != null) {
                    String department = crew.getDepartment();
                    String text = "";
                    if (department.equals("Directing")) {
                        text = "导演\n" + crew.getName();
                    } else if (department.equals("Writing")) {
                        text = "编剧\n" + crew.getName();
                    }
                    if (!text.equals("")) {
                        creditAdapter.text.add(text);
                        creditAdapter.image.add(crew.getProfile_path());
                    }
                }
            }

            for (int i = 0; i < castBeen.size(); i++) {
                MovieCredits.CastBean cast = castBeen.get(i);
                if (cast.getProfile_path() != null) {
                    if (!cast.getCharacter().contains("uncredited")) {
                        String text = cast.getName() + " 饰演\n" + cast.getCharacter();
                        creditAdapter.text.add(text);
                        creditAdapter.image.add(cast.getProfile_path());
                    }
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    creditAdapter.notifyDataSetChanged();
                    dismiss(R.id.creditProgressBar);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View recyclerView = findViewById(R.id.creditRecyclerView);
                    if (recyclerView == null) return;
                    Snackbar snackbar = Snackbar.make(recyclerView, "获取演员失败", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    dismiss(R.id.creditProgressBar);
                }
            });
        }
    }

    private void getMovieVideos(final int id) {
        Log.d("getMovieVideos", "电影id:" + id);
        String key = getString(R.string.key);
        String url = "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + key;
        String response;
        Log.v("url", url);
        response = common.httpGET(url);
        Log.d("http", response);

//        Gson gson = new Gson();
//        final MovieImages json = gson.fromJson(response, MovieImages.class);
//        if (json != null) {
//            backdropsAdapter.bean = json.getBackdrops();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //设置工具栏背景图为海报图
//                    ImageView toolbarImageView = (ImageView) findViewById(R.id.toolbarImageView);
//                    List<MovieImages.BackdropsBean> data = json.getBackdrops();
//                    String path = "https://image.tmdb.org/t/p/w370" + data.get(new Random().nextInt(data.size())).getFile_path();
//                    Picasso.with(backdropsAdapter.context)
//                            .load(path)
//                            .into(toolbarImageView);
//                    backdropsAdapter.notifyDataSetChanged();
//                    dismiss(R.id.backdropsProgressBar);
//                }
//            });
//        } else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    View recyclerView = findViewById(R.id.backdropsRecyclerView);
//                    if (recyclerView == null) return;
//                    Snackbar snackbar = Snackbar.make(recyclerView, "获取剧照失败", Snackbar.LENGTH_SHORT);
//                    snackbar.show();
//                    dismiss(R.id.backdropsProgressBar);
//                }
//            });
//        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.youtubeTextView)).setMovementMethod(LinkMovementMethod.getInstance());
                ((TextView) findViewById(R.id.youtubeTextView)).setText(Html.fromHtml("<a href=http://www.youtube.com/watch?v=PfBVIHgQbYk>Official Trailer</a>"));

            }
        });
    }

    class BackdropsAdapter extends RecyclerView.Adapter<BackdropsAdapter.CustomViewHolder> {
        List<MovieImages.BackdropsBean> bean = new ArrayList<>();
        Context context;

        BackdropsAdapter(Context c) {
            context = c;
        }

        void clear() {
            bean.clear();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            View view;

            CustomViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                imageView = (ImageView) itemView.findViewById(R.id.backdropsImageView);
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.image_backdrop, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder holder, final int position) {
            String postPrefix = "https://image.tmdb.org/t/p/w342";
            Picasso.with(context)
                    .load(postPrefix + bean.get(position).getFile_path())
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            dismiss(holder.view, R.id.backdropsImageProgressBar);
                        }

                        @Override
                        public void onError() {
                            dismiss(holder.view, R.id.backdropsImageProgressBar);
                            Snackbar snackbar = Snackbar.make(holder.imageView, "获取图片失败", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            dismiss(R.id.creditProgressBar);
                        }
                    });
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

    class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.CustomViewHolder> {
        List<String> text = new ArrayList<>();
        List<String> image = new ArrayList<>();
        Context context;

        CreditAdapter(Context c) {
            context = c;
        }

        void clear() {
            text.clear();
            image.clear();
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.image_credit, viewGroup, false);
            return new CustomViewHolder(view);
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;
            View view;

            CustomViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.creditImageView);
                textView = (TextView) itemView.findViewById(R.id.creditTextView);
                view = itemView;
            }
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder holder, int position) {
            holder.textView.setText(text.get(position));
            final String imageUrl = "https://image.tmdb.org/t/p/w300" + image.get(position);
            ImageView imageView = holder.imageView;
            Picasso.with(context)
                    .load(imageUrl)
                    .into(imageView);

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return text.size();
        }
    }
}
