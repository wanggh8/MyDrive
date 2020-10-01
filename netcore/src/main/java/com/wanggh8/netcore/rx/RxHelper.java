package com.wanggh8.netcore.rx;

import com.wanggh8.netcore.bean.JSONBean;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxHelper {


    public static <T> ObservableTransformer<JSONBean, T> handleJSONResult() {
        return new ObservableTransformer<JSONBean, T>() {
            @Override
            public ObservableSource<T> apply(Observable<JSONBean> upstream) {
                return upstream.map(new Function<JSONBean, T>() {
                    @Override
                    public T apply(JSONBean jsonBean) throws Exception {
                        return (T) jsonBean;
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

            }
        };
    }


}