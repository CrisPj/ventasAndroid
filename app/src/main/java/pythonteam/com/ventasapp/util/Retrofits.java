package pythonteam.com.ventasapp.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.JsonParseException;


import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import pythonteam.com.ventasapp.api.WebService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofits {
    private static WebService mRetrofits;

    private Retrofits()
    {

    }

    public static WebService get()
    {
        return mRetrofits;
    }

    public static void init(Context context)
    {
        File httpCacheDirectory = new File(context.getCacheDir(), "httpCache");
        Cache cache = new Cache(httpCacheDirectory, 30 * 1024 * 1024);
        String url = "http://192.168.0.14:8080/ventas/api/";
        if(mRetrofits == null) {

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.cache(cache)
                    .addInterceptor(chain -> {
                        try {
                            return chain.proceed(chain.request());
                        } catch (Exception e) {
                            Request offlineRequest = chain.request().newBuilder()
                                    .header("Cache-Control", "public, only-if-cached," +
                                            "max-stale=" + 60 * 60 * 24)
                                    .build();
                            return chain.proceed(offlineRequest);
                        }
                    });

            //PAra el token
            String token = SharedPreferencesManager.read(SharedPreferencesManager.TOKEN,"");
            httpClient.interceptors().add(chain -> {
                Request originalRequest = chain.request();
                Request.Builder builder1 = originalRequest.newBuilder().header("Authorization", String.format("Bearer %s", token));
                Request newRequest = builder1.build();
                return chain.proceed(newRequest);
            });

            mRetrofits = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build()
                    .create(WebService.class);
        }
    }
}
