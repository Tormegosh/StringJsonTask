package ru.clevertec;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonSerializer {
    public String serialize(Object instance) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        Field[] fields = instance.getClass().getDeclaredFields();
        boolean firstFieldAdded = false;
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(instance);
                if (value == null) {
                    continue;
                }
                if (firstFieldAdded) {
                    jsonBuilder.append(", ");
                }
                jsonBuilder.append("\"").append(field.getName()).append("\": ");

                if (value instanceof Map) {
                    jsonBuilder.append(serializeMap((Map<?, ?>) value));
                } else if (value instanceof List) {
                    jsonBuilder.append(serializeList((List<?>) value));
                } else {
                    jsonBuilder.append("\"").append(value).append("\"");
                }
                firstFieldAdded = true;
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field: " + field.getName(), e);
            }
        }
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    private String serializeList(List<?> list) {
        StringBuilder listBuilder = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            listBuilder.append(serialize(item));
            if (i < list.size() - 1) {
                listBuilder.append(", ");
            }
        }
        listBuilder.append("]");
        return listBuilder.toString();
    }

    private String serializeMap(Map<?, ?> map) {
        StringBuilder mapBuilder = new StringBuilder("{");
        Iterator<?> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iterator.next();
            mapBuilder.append("\"").append(entry.getKey().toString()).append("\": ")
                    .append(entry.getValue().toString());
            if (iterator.hasNext()) {
                mapBuilder.append(", ");
            }
        }
        mapBuilder.append("}");
        return mapBuilder.toString();
    }
}
