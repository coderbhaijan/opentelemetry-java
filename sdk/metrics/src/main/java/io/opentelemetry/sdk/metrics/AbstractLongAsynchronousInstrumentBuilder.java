/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.sdk.metrics;

import io.opentelemetry.api.metrics.AsynchronousInstrument;
import io.opentelemetry.sdk.metrics.common.InstrumentType;
import io.opentelemetry.sdk.metrics.common.InstrumentValueType;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;

abstract class AbstractLongAsynchronousInstrumentBuilder<B extends AbstractInstrument.Builder<?>>
    extends AbstractInstrument.Builder<B> {
  private final MeterProviderSharedState meterProviderSharedState;
  private final MeterSharedState meterSharedState;
  @Nullable private Consumer<AsynchronousInstrument.LongResult> updater;

  AbstractLongAsynchronousInstrumentBuilder(
      String name,
      InstrumentType instrumentType,
      InstrumentValueType instrumentValueType,
      MeterProviderSharedState meterProviderSharedState,
      MeterSharedState meterSharedState) {
    super(name, instrumentType, instrumentValueType);
    this.meterProviderSharedState = meterProviderSharedState;
    this.meterSharedState = meterSharedState;
  }

  public B setUpdater(Consumer<AsynchronousInstrument.LongResult> updater) {
    this.updater = updater;
    return getThis();
  }

  final <I extends AbstractInstrument> I buildInstrument(
      BiFunction<InstrumentDescriptor, AsynchronousInstrumentAccumulator, I> instrumentFactory) {
    InstrumentDescriptor descriptor = buildDescriptor();
    InstrumentProcessor processor =
        meterProviderSharedState
            .getViewRegistry()
            .createBatcher(meterProviderSharedState, meterSharedState, descriptor);
    return meterSharedState
        .getInstrumentRegistry()
        .register(
            instrumentFactory.apply(
                descriptor,
                AsynchronousInstrumentAccumulator.longAsynchronousAccumulator(processor, updater)));
  }
}
