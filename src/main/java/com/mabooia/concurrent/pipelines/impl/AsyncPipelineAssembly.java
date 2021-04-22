package com.mabooia.concurrent.pipelines.impl;

import com.mabooia.collections.streams.Stream;
import com.mabooia.concurrent.pipelines.PipelineAssembly;
import com.mabooia.concurrent.pipelines.Pipeline;
import com.mabooia.concurrent.pipelines.PipelineBuilder;
import com.mabooia.concurrent.pipelines.PipelineConsumer;
import com.mabooia.concurrent.pipelines.PipelineLink;
import com.mabooia.concurrent.pipelines.PipelineProducer;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class AsyncPipelineAssembly implements PipelineAssembly {

    private final LinkedList<Pipeline> steps;

    private AsyncPipelineAssembly(final LinkedList<Pipeline> steps) {
        this.steps = steps;
    }

    @Override
    public PipelineAssembly start() {
        steps.getFirst().start();
        return this;
    }

    @Override
    public PipelineAssembly cancel() {
        steps.getFirst().cancel();
        return this;
    }

    @Override
    public void await() {
        steps.getLast().await();
    }

    // builders

    public static Builder builder(final PipelineBuilder pipelineBuilder) {
        return new Builder(pipelineBuilder);
    }

    public static final class Builder {

        private final PipelineBuilder pipelineBuilder;

        public <A> StartingBuilder<A> startPoint(final Supplier<A> supplier) {
            return new StartingBuilder<>(pipelineBuilder, supplier);
        }

        public <A> StartingBuilder<A> startPoint(final Stream<A> stream) {
            return new StartingBuilder<>(pipelineBuilder, stream);
        }

        private Builder(PipelineBuilder pipelineBuilder) {
            this.pipelineBuilder = pipelineBuilder;
        }
    }

    public static final class StartingBuilder<A> {

        private final PipelineBuilder pipelineBuilder;
        private final PipelineProducer<A> startPoint;

        public <B> StepBuilder<A, B> step(final Function<A, B> f) {
            final LinkedList<Pipeline> steps = new LinkedList<>();
            steps.addLast(startPoint);
            return new StepBuilder<>(pipelineBuilder, steps, startPoint, f);
        }

        public EndingBuilder<A> lastStep(final Consumer<A> consumer) {
            final LinkedList<Pipeline> steps = new LinkedList<>();
            steps.addLast(startPoint);
            return new EndingBuilder<>(pipelineBuilder, steps, startPoint, consumer);
        }

        private StartingBuilder(final PipelineBuilder pipelineBuilder,
                                final Supplier<A> supplier) {
            this.pipelineBuilder = pipelineBuilder;
            this.startPoint = pipelineBuilder.createProducer(supplier);
        }

        private StartingBuilder(final PipelineBuilder pipelineBuilder,
                                final Stream<A> stream) {
            this.pipelineBuilder = pipelineBuilder;
            this.startPoint = pipelineBuilder.createProducer(stream);
        }
    }

    public static final class StepBuilder<A, B> {

        private final PipelineBuilder pipelineBuilder;
        private final LinkedList<Pipeline> steps;
        private final PipelineLink<A, B> currStep;

        public <C> StepBuilder<B, C> step(final Function<B, C> f) {
            return new StepBuilder<>(pipelineBuilder, steps, currStep, f);
        }

        public EndingBuilder<B> lastStep(final Consumer<B> consumer) {
            return new EndingBuilder<>(pipelineBuilder, steps, currStep, consumer);
        }

        private StepBuilder(final PipelineBuilder pipelineBuilder,
                            final LinkedList<Pipeline> steps,
                            final PipelineProducer<A> previous,
                            final Function<A, B> f) {
            this.pipelineBuilder = pipelineBuilder;
            this.steps = steps;
            currStep = pipelineBuilder.createLink(f);
            steps.addLast(currStep);
            previous.subscribe(currStep);
        }
    }

    public static final class EndingBuilder<A> {

        private final LinkedList<Pipeline> steps;

        public PipelineAssembly build() {
            return new AsyncPipelineAssembly(steps);
        }

        private EndingBuilder(final PipelineBuilder pipelineBuilder,
                              final LinkedList<Pipeline> steps,
                              final PipelineProducer<A> previous,
                              final Consumer<A> consumer) {
            this.steps = steps;
            final PipelineConsumer<A> lastStep = pipelineBuilder.createConsumer(consumer);
            steps.addLast(lastStep);
            previous.subscribe(lastStep);
        }
    }
}
