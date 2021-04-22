package com.mabooia.concurrent.pipelines;

import java.util.function.Consumer;

public interface PipelineSupplier<A> extends Pipeline {
    void get(Consumer<A> onPublish);
}
