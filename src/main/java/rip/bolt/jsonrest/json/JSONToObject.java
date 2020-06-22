package rip.bolt.jsonrest.json;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import rip.bolt.jsonrest.annotation.PropertyField;

public class JSONToObject {

    public static <T> List<T> performList(String data, Class<T> clazz) {
        try {
            JSONArray array = new JSONArray(data);
            List<T> instance = new ArrayList<T>();

            for (int i = 0; i < array.length(); i++)
                instance.add(completeObject(array.getJSONObject(i), clazz));

            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T perform(String data, Class<T> clazz) {
        if (isTypePrimitive(clazz))
            return (T) data;

        return completeObject(new JSONObject(data), clazz);
    }

    private static <T> T completeObject(JSONObject object, Class<T> clazz) {
        try {
            T complete = clazz.newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if ((field.getModifiers() & Modifier.FINAL) > 0) // field is final
                    continue;
                field.setAccessible(true);
                Object value = object.get(getFieldName(field));

                if (isTypePrimitive(field.getType())) {
                    field.set(complete, value);
                    continue;
                }

                if (field.getType().isEnum()) {
                    field.set(complete, Enum.valueOf((Class<Enum>) field.getType(), value.toString()));
                    continue;
                }

                field.set(complete, completeObject((JSONObject) value, field.getType()));
            }

            return complete;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

        return false;
    }

    private static String getFieldName(Field field) {
        PropertyField propertyField = field.getAnnotation(PropertyField.class);
        if (propertyField == null)
            return field.getName();

        return propertyField.name();
    }

}
