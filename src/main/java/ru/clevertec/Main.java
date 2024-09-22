package ru.clevertec;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static ru.clevertec.JsonDeserializer.parseJson;

public class Main {

    public static void displayCustomerOrders(Customer customer) {
        System.out.println("Customer: " + customer.getFirstName() + " " + customer.getLastName());
        for (Order order : customer.getOrders()) {
            System.out.println("Order ID: " + order.getId());
            System.out.println("Create Date: " + order.getCreateDate());
            System.out.println("Products:");
            for (Product product : order.getProducts()) {
                System.out.println(" - Product ID: " + product.getId());
                System.out.println("   Name: " + product.getName());
                System.out.println("   Price: " + product.getPrice());
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Product product1 = new Product(UUID.randomUUID(), "Carrot", new HashMap<>() {{
            put(UUID.randomUUID(), BigDecimal.valueOf(100.00));
        }});
        Product product2 = new Product(UUID.randomUUID(), "Potato", new HashMap<>() {{
            put(UUID.randomUUID(), BigDecimal.valueOf(200.00));
        }});
        Product product3 = new Product(UUID.randomUUID(), "Cabbage", new HashMap<>() {{
            put(UUID.randomUUID(), BigDecimal.valueOf(300.00));
        }});

        Order order = new Order(UUID.randomUUID(), Arrays.asList(product1, product2, product3), OffsetDateTime.now());

        Customer customer = new Customer(UUID.randomUUID(), "Petrov", "Petr",
                LocalDate.of(1997, 11, 3), Collections.singletonList(order));

        JsonSerializer jsonSerializer = new JsonSerializer();
        String serializedJson = jsonSerializer.serialize(customer);
        System.out.println("Serialized JSON:");
        System.out.println(serializedJson);
        System.out.println("Deserialized JSON:");
        try {
            customer = parseJson(serializedJson);
            displayCustomerOrders(customer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
