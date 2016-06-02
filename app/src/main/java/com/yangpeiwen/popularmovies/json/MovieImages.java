package com.yangpeiwen.popularmovies.json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ypw on 16/5/31.
 */

public class MovieImages {

    /**
     * id : 550
     */

    private int id;
    /**
     * file_path : /8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg
     * width : 1280
     * height : 720
     * iso_639_1 : null
     * aspect_ratio : 1.78
     * vote_average : 6.647058823529412
     * vote_count : 17
     */

    private List<BackdropsBean> backdrops;
    /**
     * file_path : /2lECpi35Hnbpa4y46JX0aY3AWTy.jpg
     * width : 1000
     * height : 1500
     * iso_639_1 : en
     * aspect_ratio : 0.67
     * vote_average : 6.1395348837209305
     * vote_count : 43
     */

    private List<PostersBean> posters;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<BackdropsBean> getBackdrops() {
        return backdrops;
    }

    public void setBackdrops(List<BackdropsBean> backdrops) {
        this.backdrops = backdrops;
    }

    public List<PostersBean> getPosters() {
        return posters;
    }

    public void setPosters(List<PostersBean> posters) {
        this.posters = posters;
    }

    public static class BackdropsBean implements Serializable {
        private String file_path;
        private int width;
        private int height;
        private Object iso_639_1;
        private double aspect_ratio;
        private double vote_average;
        private int vote_count;

        public String getFile_path() {
            return file_path;
        }

        public void setFile_path(String file_path) {
            this.file_path = file_path;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public Object getIso_639_1() {
            return iso_639_1;
        }

        public void setIso_639_1(Object iso_639_1) {
            this.iso_639_1 = iso_639_1;
        }

        public double getAspect_ratio() {
            return aspect_ratio;
        }

        public void setAspect_ratio(double aspect_ratio) {
            this.aspect_ratio = aspect_ratio;
        }

        public double getVote_average() {
            return vote_average;
        }

        public void setVote_average(double vote_average) {
            this.vote_average = vote_average;
        }

        public int getVote_count() {
            return vote_count;
        }

        public void setVote_count(int vote_count) {
            this.vote_count = vote_count;
        }
    }

    public static class PostersBean {
        private String file_path;
        private int width;
        private int height;
        private String iso_639_1;
        private double aspect_ratio;
        private double vote_average;
        private int vote_count;

        public String getFile_path() {
            return file_path;
        }

        public void setFile_path(String file_path) {
            this.file_path = file_path;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getIso_639_1() {
            return iso_639_1;
        }

        public void setIso_639_1(String iso_639_1) {
            this.iso_639_1 = iso_639_1;
        }

        public double getAspect_ratio() {
            return aspect_ratio;
        }

        public void setAspect_ratio(double aspect_ratio) {
            this.aspect_ratio = aspect_ratio;
        }

        public double getVote_average() {
            return vote_average;
        }

        public void setVote_average(double vote_average) {
            this.vote_average = vote_average;
        }

        public int getVote_count() {
            return vote_count;
        }

        public void setVote_count(int vote_count) {
            this.vote_count = vote_count;
        }
    }
}
