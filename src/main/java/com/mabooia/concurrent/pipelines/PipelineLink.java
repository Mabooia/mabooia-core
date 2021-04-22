package com.mabooia.concurrent.pipelines;

public interface PipelineLink<M, R>
    extends PipelineConsumer<M>, PipelineProducer<R> {
}
