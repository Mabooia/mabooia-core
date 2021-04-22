package com.mabooia.collections.streams.impl.parallel;

import com.mabooia.collections.streams.Stream;
import com.mabooia.concurrent.Tasks;
import com.mabooia.concurrent.pipelines.PipelineAssembly;
import com.mabooia.concurrent.pipelines.PipelineBuilder;
import com.mabooia.concurrent.pipelines.impl.AsyncPipelineAssembly;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ParallelStreamOperations<A> {

    private final Stream<A> upstream;
    private final PipelineBuilder pipelineBuilder;

    public ParallelStreamOperations(final Stream<A> upstream) {
        this.upstream = upstream;
        pipelineBuilder = new PipelineBuilder(Executors.newCachedThreadPool());
    }

    public boolean allMatch(final Predicate<? super A> p) {

        final AtomicBoolean res = new AtomicBoolean(true);
        final MutableObject<PipelineAssembly> pipeAsmRef = new MutableObject<>(null);

        final PipelineAssembly pipeAsm = AsyncPipelineAssembly
            .builder(pipelineBuilder)
            .startPoint(upstream)
            .lastStep(msg -> {
                if (res.get() && !p.test(msg)) {
                    res.set(false);
                    pipeAsmRef.getValue().cancel();
                }
            })
            .build();

        pipeAsmRef.setValue(pipeAsm);
        pipeAsm
            .start()
            .await();

        return res.get();
    }

    public void forEach(final Consumer<? super A> consumer) {
        AsyncPipelineAssembly
            .builder(pipelineBuilder)
            .startPoint(upstream)
            .lastStep(consumer::accept)
            .build()
            .start()
            .await();
    }

    public <B> B reduce(final B initialValue,
                        final BiFunction<? super B, ? super A, B> f,
                        final BiFunction<? super B, ? super B, B> combine) {
        final AtomicReference<B> res = new AtomicReference<>(initialValue);
        AsyncPipelineAssembly
            .builder(pipelineBuilder)
            .startPoint(upstream)
            .step(it -> f.apply(initialValue, it))
            .lastStep(partial -> Tasks.asyncCombine(res, partial, combine::apply))
            .build()
            .start()
            .await();
        return res.get();
    }

}
