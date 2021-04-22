package com.mabooia.concurrent.pipelines;

public interface PipelineAssembly {

    PipelineAssembly start();
    PipelineAssembly cancel();
    void await();
}
