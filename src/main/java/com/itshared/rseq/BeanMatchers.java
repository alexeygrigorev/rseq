package com.itshared.rseq;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import com.itshared.rseq.ReflectionUtils.ValueExtractor;

public class BeanMatchers {

    private BeanMatchers() {
    }

    public static <O, V> EnhancedMatcher<O> eq(final Class<O> otherClass, final String propertyName,
            final V otherValue) {
        final ValueExtractor<O, V> extractor = ReflectionUtils.property(otherClass, propertyName);

        return new EnhancedMatcher<O>() {
            @Override
            public boolean match(O object) {
                V value = extractor.get(object);
                return Objects.equals(value, otherValue);
            }

            @Override
            public String toString() {
                return otherClass.getSimpleName() + "." + propertyName + " == " + otherValue;
            }
        };
    }

    public static <E> EnhancedMatcher<E> regex(final Class<E> otherClass, final String propertyName,
            final String regex) {
        final ValueExtractor<E, String> extractor = ReflectionUtils.property(otherClass, propertyName);
        final Pattern pattern = Pattern.compile(regex);

        return new EnhancedMatcher<E>() {
            @Override
            public boolean match(E e) {
                String value = extractor.get(e);
                return pattern.matcher(value).matches();
            }

            public String toString() {
                return otherClass.getSimpleName() + "." + propertyName + " =~ " + regex;
            }
        };
    }

    public static <O, V> EnhancedMatcher<O> in(final Class<O> otherClass, final String propertyName,
            final Set<V> values) {
        final ValueExtractor<O, V> extractor = ReflectionUtils.property(otherClass, propertyName);

        return new EnhancedMatcher<O>() {
            @Override
            public boolean match(O object) {
                V value = extractor.get(object);
                return values.contains(value);
            }

            @Override
            public String toString() {
                return otherClass.getSimpleName() + "." + propertyName + " in " + values;
            }
        };
    }

}
