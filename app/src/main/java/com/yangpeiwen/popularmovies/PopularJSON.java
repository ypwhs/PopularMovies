package com.yangpeiwen.popularmovies;

import java.util.List;

public class PopularJSON {

    private int page;
    private int total_results;
    private int total_pages;
    /**
     * poster_path : /inVq3FRqcYIRl2la8iZikYYxFNR.jpg
     * adult : false
     * overview : 前任特种兵韦德·威尔逊得了不治之症，而自愿加入“X武器计划”获得了快速自愈能力。在饱受虐待折磨后他成功逃脱并对Ajax进行追杀。
     * release_date : 2016-02-09
     * genre_ids : [28,12,35,10749]
     * id : 293660
     * original_title : Deadpool
     * original_language : en
     * title : 死侍
     * backdrop_path : /nbIrDhOtUpdD9HKDBRy02a8VhpV.jpg
     * popularity : 91.92864
     * vote_count : 3505
     * video : false
     * vote_average : 7.2
     */

    private List<ResultsBean> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean{
        private String poster_path;
        private boolean adult;
        private String overview;
        private String release_date;
        private int id;
        private String original_title;
        private String original_language;
        private String title;
        private String backdrop_path;
        private double popularity;
        private int vote_count;
        private boolean video;
        private double vote_average;
        private List<Integer> genre_ids;

        public String getPoster_path() {
            return poster_path;
        }

        public void setPoster_path(String poster_path) {
            this.poster_path = poster_path;
        }

        public boolean isAdult() {
            return adult;
        }

        public void setAdult(boolean adult) {
            this.adult = adult;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public String getRelease_date() {
            return release_date;
        }

        public void setRelease_date(String release_date) {
            this.release_date = release_date;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOriginal_title() {
            return original_title;
        }

        public void setOriginal_title(String original_title) {
            this.original_title = original_title;
        }

        public String getOriginal_language() {
            return original_language;
        }

        public void setOriginal_language(String original_language) {
            this.original_language = original_language;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBackdrop_path() {
            return backdrop_path;
        }

        public void setBackdrop_path(String backdrop_path) {
            this.backdrop_path = backdrop_path;
        }

        public double getPopularity() {
            return popularity;
        }

        public void setPopularity(double popularity) {
            this.popularity = popularity;
        }

        public int getVote_count() {
            return vote_count;
        }

        public void setVote_count(int vote_count) {
            this.vote_count = vote_count;
        }

        public boolean isVideo() {
            return video;
        }

        public void setVideo(boolean video) {
            this.video = video;
        }

        public double getVote_average() {
            return vote_average;
        }

        public void setVote_average(double vote_average) {
            this.vote_average = vote_average;
        }

        public List<Integer> getGenre_ids() {
            return genre_ids;
        }

        public void setGenre_ids(List<Integer> genre_ids) {
            this.genre_ids = genre_ids;
        }
    }
}
