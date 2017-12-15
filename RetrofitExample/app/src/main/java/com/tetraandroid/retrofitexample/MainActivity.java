package com.tetraandroid.retrofitexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tetraandroid.retrofitexample.http.TwitchAPI;
import com.tetraandroid.retrofitexample.http.apimodel.Top;
import com.tetraandroid.retrofitexample.http.apimodel.Twitch;
import com.tetraandroid.retrofitexample.root.App;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Inject
    TwitchAPI twitchAPI;

    private final String KEY = "taftdv10sphi8xnpfk6n7j82au9gur";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((App) getApplication()).getComponent().inject(this);

        Call<Twitch> call = twitchAPI.getTopGames(KEY);

        call.enqueue(new Callback<Twitch>() {
            @Override
            public void onResponse(Call<Twitch> call, Response<Twitch> response) {
                List<Top> gameList = response.body().getTop();

                for (Top top : gameList) {
                    System.out.println(top.getGame().getName());
                }
            }

            @Override
            public void onFailure(Call<Twitch> call, Throwable t) {
                t.printStackTrace();
            }
        });


        twitchAPI.getTopGamesObservable(KEY)
                .flatMap(new Function<Twitch, ObservableSource<Top>>() {
                    @Override
                    public ObservableSource<Top> apply(Twitch twitch) throws Exception {
                        return io.reactivex.Observable.fromIterable(twitch.getTop());
                    }
                })
                .flatMap(new Function<Top, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Top top) throws Exception {
                        return io.reactivex.Observable.just(top.getGame().getName());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.e("Main", "game is : " + s);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e("Main", "loading finished!");
                    }
                });
    }
}
