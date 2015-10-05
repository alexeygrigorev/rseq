package com.alexeygrigorev.rseq;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import com.alexeygrigorev.rseq.ReflectionUtils.ValueExtractor;

/**
 * Utility class with convenience methods for creating matchers that work on
 * java-bean classes: that is, classes with getters that follow the bean
 * convention. It can use reflection to compare the content of the java bean
 * properties:<br>
 * 
 * <ul>
 * <li>to test equality, {@link #eq(Class, String, Object)} method,</li>
 * <li>regular expression match - only for string properties,
 * {@link #regex(Class, String, String)} method</li>
 * <li>in set - to check if the property values is contained is some set,
 * {@link #in(Class, String, Set)} method</li>
 * </ul>
 * 
 * @author Alexey Grigorev
 *
 */
public class BeanMatchers {

    private BeanMatchers() {
    }

    /**
     * Creates a matcher that checks if the java bean's property is equal to the
     * provided value
     * 
     * @param otherClass java bean's class
     * @param propertyName property of the java bean
     * @param otherValue value to check against
     */
    public static <O, V> XMatcher<O> eq(final Class<O> otherClass, final String propertyName,
            final V otherValue) {
        final ValueExtractor<O, V> extractor = ReflectionUtils.property(otherClass, propertyName);

        return new ParentMatcher<O>() {
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

    public static <E> XMatcher<E> regex(final Class<E> otherClass, final String propertyName,
            final String regex) {
        final ValueExtractor<E, String> extractor = ReflectionUtils.property(otherClass, propertyName);
        final Pattern pattern = Pattern.compile(regex);

        return new ParentMatcher<E>() {
            @Override
            public boolean match(E object) {
                String value = extractor.get(object);
                return pattern.matcher(value).matches();
            }

            public String toString() {
                return otherClass.getSimpleName() + "." + propertyName + " =~ " + pattern.pattern();
            }
        };
    }

    public static <O, V> XMatcher<O> in(final Class<O> otherClass, final String propertyName,
            final Set<V> values) {
        final ValueExtractor<O, V> extractor = ReflectionUtils.property(otherClass, propertyName);

        return new ParentMatcher<O>() {
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
