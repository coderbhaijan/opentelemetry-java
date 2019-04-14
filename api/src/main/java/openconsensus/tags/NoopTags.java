/*
 * Copyright 2019, OpenConsensus Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package openconsensus.tags;

import java.util.Collections;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import openconsensus.context.NoopScope;
import openconsensus.context.Scope;
import openconsensus.internal.Utils;
import openconsensus.tags.data.TagKey;
import openconsensus.tags.data.TagMetadata;
import openconsensus.tags.data.TagValue;
import openconsensus.tags.propagation.BinaryFormat;
import openconsensus.tags.propagation.PropagationComponent;
import openconsensus.tags.propagation.TextFormat;

/** No-op implementations of tagging classes. */
final class NoopTags {

  private NoopTags() {}

  /**
   * Returns a {@code TagsComponent} that has a no-op implementation for {@link Tagger}.
   *
   * @return a {@code TagsComponent} that has a no-op implementation for {@code Tagger}.
   */
  static TagsComponent newNoopTagsComponent() {
    return new NoopTagsComponent();
  }

  @ThreadSafe
  private static final class NoopTagsComponent extends TagsComponent {
    private static final PropagationComponent PROPAGATION_COMPONENT =
        new NoopPropagationComponent();
    private static final Tagger TAGGER = new NoopTagger();

    @Override
    public Tagger getTagger() {
      return TAGGER;
    }

    @Override
    public PropagationComponent getTagPropagationComponent() {
      return PROPAGATION_COMPONENT;
    }
  }

  @Immutable
  private static final class NoopTagger extends Tagger {
    private static final TagMap EMPTY = new NoopTagMap();

    @Override
    public TagMap empty() {
      return EMPTY;
    }

    @Override
    public TagMap getCurrentTagMap() {
      return EMPTY;
    }

    @Override
    public TagMapBuilder emptyBuilder() {
      return new NoopTagMapBuilder();
    }

    @Override
    public TagMapBuilder toBuilder(TagMap tags) {
      Utils.checkNotNull(tags, "tags");
      return new NoopTagMapBuilder();
    }

    @Override
    public TagMapBuilder currentBuilder() {
      return new NoopTagMapBuilder();
    }

    @Override
    public Scope withTagMap(TagMap tags) {
      Utils.checkNotNull(tags, "tags");
      return NoopScope.getInstance();
    }
  }

  @Immutable
  private static final class NoopTagMapBuilder extends TagMapBuilder {
    private static final TagMap EMPTY = new NoopTagMap();

    @Override
    public TagMapBuilder put(TagKey key, TagValue value, TagMetadata tagMetadata) {
      Utils.checkNotNull(key, "key");
      Utils.checkNotNull(value, "value");
      Utils.checkNotNull(tagMetadata, "tagMetadata");
      return this;
    }

    @Override
    public TagMapBuilder remove(TagKey key) {
      Utils.checkNotNull(key, "key");
      return this;
    }

    @Override
    public TagMap build() {
      return EMPTY;
    }

    @Override
    public Scope buildScoped() {
      return NoopScope.getInstance();
    }
  }

  @Immutable
  private static final class NoopTagMap extends TagMap {}

  @Immutable
  private static final class NoopPropagationComponent extends PropagationComponent {
    private static final BinaryFormat BINARY_FORMAT = new NoopBinaryFormat();
    private static final TextFormat TEXT_FORMAT = new NoopTextFormat();

    @Override
    public BinaryFormat getBinaryFormat() {
      return BINARY_FORMAT;
    }

    @Override
    public TextFormat getTextFormat() {
      return TEXT_FORMAT;
    }
  }

  @Immutable
  private static final class NoopBinaryFormat extends BinaryFormat {
    private static final TagMap EMPTY = new NoopTagMap();
    static final byte[] EMPTY_BYTE_ARRAY = {};

    @Override
    public byte[] toByteArray(TagMap tags) {
      Utils.checkNotNull(tags, "tags");
      return EMPTY_BYTE_ARRAY;
    }

    @Override
    public TagMap fromByteArray(byte[] bytes) {
      Utils.checkNotNull(bytes, "bytes");
      return EMPTY;
    }
  }

  @Immutable
  private static final class NoopTextFormat extends TextFormat {
    private static final TagMap EMPTY = new NoopTagMap();

    @Override
    public List<String> fields() {
      return Collections.<String>emptyList();
    }

    @Override
    public <C> void inject(TagMap tagContext, C carrier, Setter<C> setter) {
      Utils.checkNotNull(tagContext, "tagContext");
      Utils.checkNotNull(carrier, "carrier");
      Utils.checkNotNull(setter, "setter");
    }

    @Override
    public <C> TagMap extract(C carrier, Getter<C> getter) {
      Utils.checkNotNull(carrier, "carrier");
      Utils.checkNotNull(getter, "getter");
      return EMPTY;
    }
  }
}