package com.yangpeiwen.popularmovies;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class Common {

    Common() {
    }

    private OkHttpClient client = new OkHttpClient();

    String httpGET(String url) {
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
}
