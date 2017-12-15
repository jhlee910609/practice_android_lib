package com.tetraandroid.retrofitexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.tetraandroid.retrofitexample.http.TwitchAPI;
import com.tetraandroid.retrofitexample.http.apimodel.Top;
import com.tetraandroid.retrofitexample.http.apimodel.Twitch;
import com.tetraandroid.retrofitexample.root.App;

import javax.inject.Inject;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Inject
    TwitchAPI twitchAPI;

    private final String KEY = "taftdv10sphi8xnpfk6n7j82au9gur";
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((App) getApplication()).getComponent().inject(this);

        tv = (TextView) findViewById(R.id.tv);
        final StringBuilder sb = new StringBuilder();
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
                        tv.setText(sb.append("game name is : " + s + "\n").toString());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(MainActivity.this, "end loading from Twitch API", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
