package rip.bolt.jsonrest.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.json.JSONArray;
import org.json.JSONObject;

import rip.bolt.jsonrest.annotation.PropertyField;

public class ObjectToJSON {

    public static String perform(Object object) {
        if (isTypePrimitive(object.getClass()))
            return String.valueOf(object);

        if (object.getClass().isArray()) {
            JSONArray data = new JSONArray();
            Object[] array = (Object[]) object;
            for (int i = 0; i < array.length; i++)
                data.put(i, performObject(array[i]));
        }

        return performObject(object);
    }

    private static String performObject(Object object) {
        if (isTypePrimitive(object.getClass()))
            return String.valueOf(object);

        JSONObject json = new JSONObject();
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                if ((field.getModifiers() & Modifier.FINAL) > 0) // we skip this for GET, so let's skip this for POST
                    continue;
                field.setAccessible(true);

                Object value = field.get(object);
                if (isTypePrimitive(field.getType()))
                    json.put(getFieldName(field), value);
                else
                    json.put(getFieldName(field), performObject(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    private static boolean isTypePrimitive(Class<?> clazz) {
        if (clazz == boolean.class || clazz == Boolean.class)
            return true;
        if (clazz == byte.class || clazz == Byte.class)
            return true;
        if (clazz == short.class || clazz == Short.class)
            return true;
        if (clazz == char.class || clazz == Character.class)
            return true;
        if (clazz == int.class || clazz == Integer.class)
            return true;
        if (clazz == float.class || clazz == Float.class)
            return true;
        if (clazz == long.class || clazz == Long.class)
            return true;
        if (clazz == double.class || clazz == Double.class)
            return true;
        if (clazz == String.class)
            return true;
        if (clazz.isEnum())
            return true;

        return false;
    }

    private static String getFieldName(Field field) {
        PropertyField propertyField = field.getAnnotation(PropertyField.class);
        if (propertyField == null)
            return field.getName();

        return propertyField.name();
    }

}
