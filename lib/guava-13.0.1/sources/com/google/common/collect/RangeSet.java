/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * A set of values of type {@code C} made up of zero or more <i>disjoint</i> {@linkplain Range
 * ranges}.
 *
 * <p>It is guaranteed that {@linkplain Range#isConnected connected} ranges will be
 * {@linkplain Range#span coalesced} together, and that {@linkplain Range#isEmpty empty} ranges
 * will never be held in a {@code RangeSet}.
 *
 * <p>For a {@link Set} whose contents are specified by a {@link Range}, see {@link ContiguousSet}.
 *
 * @author Kevin Bourrillion
 * @author Louis Wasserman
 */ interface RangeSet<C extends Comparable> {

  /**
   * Determines whether any of this range set's member ranges contains {@code value}.
   */
  boolean contains(C value);

  /**
   * Returns the unique range from this range set that {@linkplain Range#contains contains}
   * {@code value}, or {@code null} if this range set does not contain {@code value}.
   */
  Range<C> rangeContaining(C value);

  /**
   * Returns a view of the {@linkplain Range#isConnected disconnected} ranges that make up this
   * range set.  The returned set may be empty. The iterators returned by its
   * {@link Iterable#iterator} method return the ranges in increasing order of lower bound
   * (equivalently, of upper bound).
   */
  Set<Range<C>> asRanges();

  /**
   * Returns the minimal range which {@linkplain Range#encloses(Range) encloses} all ranges
   * in this range set.
   *
   * @throws NoSuchElementException if this range set is {@linkplain #isEmpty() empty}
   */
  Range<C> span();

  /**
   * Returns {@code true} if this range set contains no ranges.
   */
  boolean isEmpty();

  /**
   * Returns a view of the complement of this {@code RangeSet}.
   *
   * <p>The returned view supports the {@link #add} operation if this {@code RangeSet} supports
   * {@link #remove}, and vice versa.
   */
  RangeSet<C> complement();

  /**
   * Adds the specified range to this {@code RangeSet} (optional operation). That is, for equal
   * range sets a and b, the result of {@code a.add(range)} is that {@code a} will be the minimal
   * range set for which both {@code a.enclosesAll(b)} and {@code a.encloses(range)}.
   *
   * <p>Note that {@code range} will be {@linkplain Range#span(Range) coalesced} with any ranges in
   * the range set that are {@linkplain Range#isConnected(Range) connected} with it.  Moreover,
   * if {@code range} is empty, this is a no-op.
   *
   * @throws UnsupportedOperationException if this range set does not support the {@code add}
   *         operation
   */
  void add(Range<C> range);

  /**
   * Removes the specified range from this {@code RangeSet} (optional operation). After this
   * operation, if {@code range.contains(c)}, {@code this.contains(c)} will return {@code false}.
   *
   * <p>If {@code range} is empty, this is a no-op.
   *
   * @throws UnsupportedOperationException if this range set does not support the {@code remove}
   *         operation
   */
  void remove(Range<C> range);

  /**
   * Returns {@code true} if there exists a member range in this range set which
   * {@linkplain Range#encloses encloses} the specified range.
   */
  boolean encloses(Range<C> otherRange);

  /**
   * Returns {@code true} if for each member range in {@code other} there exists a member range in
   * this range set which {@linkplain Range#encloses encloses} it. It follows that
   * {@code this.contains(value)} whenever {@code other.contains(value)}. Returns {@code true} if
   * {@code other} is empty.
   *
   * <p>This is equivalent to checking if this range set {@link #encloses} each of the ranges in
   * {@code other}.
   */
  boolean enclosesAll(RangeSet<C> other);

  /**
   * Adds all of the ranges from the specified range set to this range set (optional operation).
   * After this operation, this range set is the minimal range set that
   * {@linkplain #enclosesAll(RangeSet) encloses} both the original range set and {@code other}.
   *
   * <p>This is equivalent to calling {@link #add} on each of the ranges in {@code other} in turn.
   *
   * @throws UnsupportedOperationException if this range set does not support the {@code addAll}
   *         operation
   */
  void addAll(RangeSet<C> other);

  /**
   * Removes all of the ranges from the specified range set from this range set (optional
   * operation). After this operation, if {@code other.contains(c)}, {@code this.contains(c)} will
   * return {@code false}.
   *
   * <p>This is equivalent to calling {@link #remove} on each of the ranges in {@code other} in
   * turn.
   *
   * @throws UnsupportedOperationException if this range set does not support the {@code removeAll}
   *         operation
   */
  void removeAll(RangeSet<C> other);

  /**
   * Returns {@code true} if {@code obj} is another {@code RangeSet} that contains the same ranges
   * according to {@link Range#equals(Object)}.
   */
  @Override
  boolean equals(@Nullable Object obj);

  /**
   * Returns a readable string representation of this range set. For example, if this
   * {@code RangeSet} consisted of {@code Ranges.closed(1, 3)} and {@code Ranges.greaterThan(4)},
   * this might return {@code " [1‥3](4‥+∞)}"}.
   */
  @Override
  String toString();
}