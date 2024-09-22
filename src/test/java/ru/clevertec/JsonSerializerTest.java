package ru.clevertec;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonSerializerTest {

    @Test
    public void testSerializeSimpleObject() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Carrot");
        product.setPrice(Map.of(UUID.randomUUID(), new BigDecimal("100.00")));

        String json = new JsonSerializer().serialize(product);
        assertEquals("{\"id\": \"" + product.getId() + "\", \"name\": \"Carrot\", \"price\": {\""
                + product.getPrice().keySet().iterator().next() + "\": 100.00}}", json);
    }

    @Test
    public void testSerializeObjectWithNullFields() {
        Customer customer = new Customer();
        customer.setFirstName(null);
        customer.setLastName("Doe");

        String json = new JsonSerializer().serialize(customer);
        assertEquals("{\"lastName\": \"Doe\"}", json);
    }

    @Test
    public void testSerializeObjectWithMap() {
        Map<UUID, BigDecimal> priceMap = new HashMap<>();
        priceMap.put(UUID.randomUUID(), new BigDecimal("200.00"));

        Product product = new Product();
        product.setPrice(priceMap);

        String json = new JsonSerializer().serialize(product);
        assertEquals("{\"price\": {\"" + priceMap.keySet().iterator().next() + "\": 200.00}}", json);
    }

    @Test
    public void testSerializeEmptyObject() {
        Product product = new Product();
        String json = new JsonSerializer().serialize(product);
        assertEquals("{}", json);
    }
}
