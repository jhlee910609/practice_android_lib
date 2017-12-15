package com.tetraandroid.retrofitexample.http;

import com.tetraandroid.retrofitexample.http.apimodel.Twitch;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface TwitchAPI {

    @GET("games/top")
    Call<Twitch> getTopGames(@Header("Client-Id") String clientId);

    @GET("games/top")
    Observable<Twitch> getTopGamesObservable(@Header("Client-Id") String clientId);
}
