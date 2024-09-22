package ru.clevertec;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JsonDeserializerTest {

    @Test
    public void testDeserializeCustomerWithValidFields() throws Exception {
        String json = "{\"firstName\": \"Ivan\", \"lastName\": \"Ivanov\", \"dateBirth\": \"1997-01-01\"}";
        Customer customer = JsonDeserializer.parseJson(json);
        assertEquals("Ivan", customer.getFirstName());
        assertEquals("Ivanov", customer.getLastName());
        assertEquals(LocalDate.of(1997, 1, 1), customer.getDateBirth());
    }

    @Test
    public void testDeserializeCustomerWithNullFields() throws IOException {
        String json = "{\"firstName\": null, \"lastName\": null, \"dateBirth\": null}";
        Customer customer = JsonDeserializer.parseJson(json);
        assertNull(customer.getFirstName());
        assertNull(customer.getLastName());
        assertNull(customer.getDateBirth());
    }

    @Test
    public void testDeserializeCustomerWithOrders() throws Exception {
        String json = "{\"firstName\": \"Ivan\", " +
                "\"lastName\": \"Ivanov\", " +
                "\"dateBirth\": \"1990-05-15\", " +
                "\"orders\": [{" +
                "\"id\": \"550e8400-e29b-41d4-a716-446655440001\", " +
                "\"products\": [{" +
                "\"idProduct\": \"550e8400-e29b-41d4-a716-446655440002\", " +
                "\"name\": \"Carrot\", " +
                "\"price\": {\"550e8400-e29b-41d4-a716-446655440003\": 100.00}}]}]}";
        Customer customer = JsonDeserializer.parseJson(json);
        assertNotNull(customer.getOrders());
        assertEquals(1, customer.getOrders().size());
        Order order = customer.getOrders().getFirst();
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), order.getId());
        assertNotNull(order.getProducts());
        assertEquals(1, order.getProducts().size());
        Product product = order.getProducts().getFirst();
        assertEquals("Carrot", product.getName());
        assertEquals(0, product.getPrice().get(UUID.fromString("550e8400-e29b-41d4-a716-446655440003")).compareTo(new BigDecimal("100.00")));
    }

    @Test
    public void testDeserializeProductWithNullPrice() throws Exception {
        String json = "{\"idProduct\": \"550e8400-e29b-41d4-a716-446655440002\", \"name\": \"Carrot\", \"price\": null}";
        Product product = JsonDeserializer.deserialize(new ObjectMapper().readTree(json), Product.class);
        assertNotNull(product);
        assertEquals("Carrot", product.getName());
        assertNull(product.getPrice());
    }

    @Test
    public void testDeserializeOrderWithEmptyProducts() throws Exception {
        String json = "{\"id\": \"550e8400-e29b-41d4-a716-446655440001\", \"products\": []}";
        Order order = JsonDeserializer.deserialize(new ObjectMapper().readTree(json), Order.class);
        assertNotNull(order);
        assertEquals(0, order.getProducts().size());
    }

    @Test
    public void testDeserializeProductWithPriceMap() throws Exception {
        String json = "{\"idProduct\": \"550e8400-e29b-41d4-a716-446655440002\", \"name\": \"Carrot\", \"price\": {\"550e8400-e29b-41d4-a716-446655440003\": 100.00}}";
        Product product = JsonDeserializer.deserialize(new ObjectMapper().readTree(json), Product.class);
        assertNotNull(product);
        assertEquals("Carrot", product.getName());
        assertEquals(0, product.getPrice().get(UUID.fromString("550e8400-e29b-41d4-a716-446655440003")).compareTo(new BigDecimal("100.00")));
    }
}
