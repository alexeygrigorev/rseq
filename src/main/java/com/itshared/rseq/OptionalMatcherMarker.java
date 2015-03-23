package com.itshared.rseq;

/**
 * Marker interface for marking matchers that can accept no input. It's needed when we
 * run into the end of sequence and need to check if all the remaining matchers are
 * optional or not.
 * 
 * @author Alexey Grigorev
 *
 */
interface OptionalMatcherMarker {}
