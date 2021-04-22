package com.mabooia.concurrent.pipelines;

public interface PipelineConsumer<T> extends Pipeline {
    void push(T message);
}
