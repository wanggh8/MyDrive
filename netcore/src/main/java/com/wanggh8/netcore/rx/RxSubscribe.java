
package com.wanggh8.netcore.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class RxSubscribe<T> implements Observer<T> {
    Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
        _onStart();
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        _onError(e.getMessage());
    }

    @Override
    public void onComplete() {
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
    protected abstract void _onNext(T t);

    protected abstract void _onError(String message);

    protected abstract void _onStart();

}