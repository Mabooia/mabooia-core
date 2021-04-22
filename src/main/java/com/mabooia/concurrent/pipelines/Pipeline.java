package com.mabooia.concurrent.pipelines;

public interface Pipeline {

    void await();

    void start();

    void notifyStop();

    void cancel();
}
