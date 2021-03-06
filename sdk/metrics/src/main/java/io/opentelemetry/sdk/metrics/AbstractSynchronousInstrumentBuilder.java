/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics;

import io.opentelemetry.sdk.metrics.aggregator.Aggregator;
import io.opentelemetry.sdk.metrics.common.InstrumentType;
import io.opentelemetry.sdk.metrics.common.InstrumentValueType;
import java.util.function.BiFunction;
import java.util.function.Function;

abstract class AbstractSynchronousInstrumentBuilder<
        B extends AbstractSynchronousInstrumentBuilder<?>>
    extends AbstractInstrument.Builder<B> {
  private final MeterProviderSharedState meterProviderSharedState;
  private final MeterSharedState meterSharedState;

  AbstractSynchronousInstrumentBuilder(
      String name,
      InstrumentType instrumentType,
      InstrumentValueType instrumentValueType,
      MeterProviderSharedState meterProviderSharedState,
      MeterSharedState meterSharedState) {
    super(name, instrumentType, instrumentValueType);
    this.meterProviderSharedState = meterProviderSharedState;
    this.meterSharedState = meterSharedState;
  }

  final <I extends AbstractInstrument, BoundT extends AbstractBoundInstrument> I buildInstrument(
      Function<Aggregator, BoundT> boundFactory,
      BiFunction<InstrumentDescriptor, SynchronousInstrumentAccumulator<BoundT>, I>
          instrumentFactory) {
    InstrumentDescriptor descriptor = buildDescriptor();
    return meterSharedState
        .getInstrumentRegistry()
        .register(instrumentFactory.apply(descriptor, buildAccumulator(descriptor, boundFactory)));
  }

  private <BoundT extends AbstractBoundInstrument>
      SynchronousInstrumentAccumulator<BoundT> buildAccumulator(
          InstrumentDescriptor descriptor, Function<Aggregator, BoundT> boundFactory) {
    InstrumentProcessor processor =
        meterProviderSharedState
            .getViewRegistry()
            .createBatcher(meterProviderSharedState, meterSharedState, descriptor);
    return new SynchronousInstrumentAccumulator<>(processor, boundFactory);
  }
}
