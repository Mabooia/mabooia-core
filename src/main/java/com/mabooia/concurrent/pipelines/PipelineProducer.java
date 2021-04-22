package com.mabooia.concurrent.pipelines;

public interface PipelineProducer<T> extends Pipeline {
    void subscribe(PipelineConsumer<T> destination);
}
