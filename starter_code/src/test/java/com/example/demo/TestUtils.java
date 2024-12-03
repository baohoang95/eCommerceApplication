package com.example.demo;

import java.lang.reflect.Field;

/**
 * Utility class to inject objects into fields of a target object using reflection.
 */
public class TestUtils {

    /**
     * Injects an object into a specified field of the target object.
     *
     * @param target    the object into which the field is injected
     * @param fieldName the name of the field to inject the object into
     * @param toInject  the object to inject into the field
     */
    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;

        try {
            // Get the field by name in the target's class
            Field field = target.getClass().getDeclaredField(fieldName);

            // Check if the field is private and make it accessible
            if (!field.isAccessible()) {
                field.setAccessible(true);
                wasPrivate = true;
            }

            // Inject the object into the field
            field.set(target, toInject);

            // If the field was private, reset its accessibility
            if (wasPrivate) {
                field.setAccessible(false);
            }

        } catch (NoSuchFieldException e) {
            // Log and print a message if the field is not found
            System.err.println("No such field: " + fieldName);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // Log and print a message if there's an access issue
            System.err.println("Illegal access to field: " + fieldName);
            e.printStackTrace();
        }
    }
}
