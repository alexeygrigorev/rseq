package com.alexeygrigorev.rseq;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

class ReflectionUtils {

    private ReflectionUtils() {
    }

    static interface ValueExtractor<O, V> {
        V get(O object);
    }

    static <O, V> ValueExtractor<O, V> property(Class<O> cls, String propertyName) {
        Map<String, Method> getters = new HashMap<String, Method>();
        Map<String, Field> fields = new HashMap<String, Field>();
        findAllProperties(cls, getters, fields);

        if (getters.containsKey(propertyName)) {
            final Method method = getters.get(propertyName);
            return new ValueExtractor<O, V>() {
                @Override
                public V get(O object) {
                    try {
                        @SuppressWarnings("unchecked")
                        V result = (V) method.invoke(object);
                        return result;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        } else if (fields.containsKey(propertyName)) {
            final Field field = fields.get(propertyName);
            return new ValueExtractor<O, V>() {
                @Override
                public V get(O object) {
                    try {
                        @SuppressWarnings("unchecked")
                        V result = (V) field.get(object);
                        return result;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }

        throw new IllegalArgumentException("The class " + cls.getName() + " does not contain property '"
                + propertyName + "'");
    }

    private static void findAllProperties(Class<?> cls, Map<String, Method> getters,
            Map<String, Field> allFields) {
        if (cls.equals(Object.class)) {
            return;
        }

        addGetters(getters, cls.getDeclaredMethods());
        addGetters(getters, cls.getMethods());
        addFields(allFields, cls.getDeclaredFields());
        addFields(allFields, cls.getFields());

        findAllProperties(cls.getSuperclass(), getters, allFields);
    }

    private static void addGetters(Map<String, Method> getters, Method[] declaredMethods) {
        for (Method method : declaredMethods) {
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            boolean isPublic = Modifier.isPublic(method.getModifiers());

            if (isStatic || !isPublic) {
                continue;
            }

            String name = method.getName();
            if (name.startsWith("get")) {
                getters.put(name.substring(3), method);
                String canonicalName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                getters.put(canonicalName, method);
            } else if (name.startsWith("is")) {
                getters.put(name.substring(2), method);
                String canonicalName = Character.toLowerCase(name.charAt(2)) + name.substring(3);
                getters.put(canonicalName, method);
            }
        }
    }

    private static void addFields(Map<String, Field> allFields, Field[] fields) {
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            allFields.put(name, field);
        }
    }

}
