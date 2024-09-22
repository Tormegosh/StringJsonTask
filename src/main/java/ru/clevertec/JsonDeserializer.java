package ru.clevertec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JsonDeserializer {

    public static <T> T deserialize(JsonNode node, Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            T instance = constructor.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (node.has(field.getName()) && !node.get(field.getName()).isNull()) {
                    if (field.getType() == UUID.class) {
                        field.set(instance, UUID.fromString(node.get(field.getName()).asText()));
                    } else if (field.getType() == LocalDate.class) {
                        field.set(instance, LocalDate.parse(node.get(field.getName()).asText()));
                    } else if (field.getType() == OffsetDateTime.class) {
                        field.set(instance, OffsetDateTime.parse(node.get(field.getName()).asText()));
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        Class<?> itemType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        List<Object> list = new ArrayList<>();

                        if (node.get(field.getName()).isArray()) {
                            for (JsonNode itemNode : node.get(field.getName())) {
                                list.add(deserialize(itemNode, itemType));
                            }
                        }
                        field.set(instance, list);
                    } else if (field.getType() == Map.class) {
                        Map<UUID, BigDecimal> priceMap = new HashMap<>();
                        JsonNode priceNode = node.get(field.getName());

                        if (priceNode.isObject()) {
                            priceNode.fields().forEachRemaining(entry -> {
                                UUID key = UUID.fromString(entry.getKey());
                                BigDecimal value = entry.getValue().decimalValue();
                                priceMap.put(key, value);
                            });
                        }
                        field.set(instance, priceMap);
                    } else {
                        field.set(instance, node.get(field.getName()).asText());
                    }
                } else {
                    field.set(instance, null);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to " + clazz.getSimpleName(), e);
        }
    }

    public static Customer parseJson(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);
        return deserialize(rootNode, Customer.class);
    }
}
