package com.mabooia.concurrent.pipelines;

import com.mabooia.Lazy;
import com.mabooia.collections.streams.Stream;
import com.mabooia.concurrent.pipelines.impl.PipelineConsumerBase;
import com.mabooia.concurrent.pipelines.impl.PipelineLinkBase;
import com.mabooia.concurrent.pipelines.impl.PipelineProducerBase;
import com.mabooia.concurrent.pipelines.impl.PipelineSupplierBase;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PipelineBuilder {

    private final ExecutorService executorService;

    public PipelineBuilder(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public <A> PipelineProducer<A> createProducer(final Stream<A> stream) {
        return new PipelineProducerBase<>(executorService) {
            @Override
            protected void executeLoop(AtomicBoolean cancelled) {
                Stream<A> curr = stream;
                Optional<A> currHead;
                while (!cancelled.get() && ((currHead = curr.getHeadIfPresent())).isPresent()) {
                    publish(currHead.get());
                    curr = curr.getTail();
                }
            }
        };
    }

    public <A> PipelineProducer<A> createProducer(final Supplier<A> supplier) {
        return new PipelineProducerBase<>(executorService) {
            @Override
            protected void executeLoop(AtomicBoolean cancelled) {
                while (!cancelled.get()) {
                    final A msg = supplier.get();
                    publish(msg);
                }
            }
        };
    }

    public <A, B> PipelineLink<A, B> createLink(final Function<A, B> f) {
        return new PipelineLinkBase<>(executorService) {
            @Override
            public B processMessage(A msg) {
                return f.apply(msg);
            }
        };
    }

    public <A> PipelineConsumer<A> createConsumer(final Consumer<A> consumer) {
        return new PipelineConsumerBase<>(executorService) {
            @Override
            protected void acceptMessage(A msg) {
                consumer.accept(msg);
            }
        };
    }

    public <A> PipelineSupplier<A> createSupplier(final Stream<A> stream, final int bufferSize) {
        return new PipelineSupplierBase<>(executorService, bufferSize) {
            private Lazy<Stream<A>> currLazy = Lazy.of(stream);

            @Override
            protected Optional<A> getNext() {
                final Stream<A> curr = currLazy.get();
                final Optional<A> head = curr.getHeadIfPresent();
                currLazy = Lazy.of(executorService.submit(curr::getTail));
                return head;
            }
        };
    }

    public <A> PipelineSupplier<A> createSupplier(final Supplier<Optional<A>> supplier, final int bufferSize) {
        return new PipelineSupplierBase<>(executorService, bufferSize) {
            @Override
            protected Optional<A> getNext() {
                return supplier.get();
            }
        };
    }
}
