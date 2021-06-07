package com.smartmarket.code.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ReadDbPropertiesPostProcessor implements EnvironmentPostProcessor {
    /**
     * Name of the custom property source added by this post processor class
     */
    private static final String PROPERTY_SOURCE_NAME = "databaseProperties";

    private String[] KEYS = {
            "excel.threads",
            "cronDelay",
            "cronDelayEmail",
            "spring.mail.username",
            "spring.mail.password",
            "spring.mail.host",
            "spring.mail.port",
            "spring.mail.properties.mail.transport.protocol",
            "spring.mail.properties.mail.smtp.auth",
            "spring.mail.properties.mail.smtp.starttls.enabled",
            "spring.mail.properties.mail.debug",
            "spring.mail.properties.mail.smtp.starttls.required",
            "spring.mail.properties.mail.socketFactory.port",
            "spring.mail.properties.mail.socketFactory.class",
            "spring.mail.properties.mail.socketFactory.fallback",
            "white.executor.threads",
            "white.search.threads",
            "lot.sync.threads",
            "lot.async.threads",
            "lot.soap.threads",
            "excel.async.threads",
            "kpi.threads",
            "upload.threads"
    };

    /**
     * Adds Spring Environment custom logic. This custom logic fetch properties from database and setting highest precedence
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        Map<String, Object> propertySource = new HashMap<>();

        try {

            // Build manually datasource to ServiceConfig
            DataSource ds = DataSourceBuilder
                    .create()
                    .username(environment.getProperty("spring.datasource.username"))
                    .password(environment.getProperty("spring.datasource.password"))
                    .url(environment.getProperty("spring.datasource.url"))
//                    .driverClassName("com.mysql.jdbc.Driver")
                    .build();

            // Fetch all properties

            Connection connection = ds.getConnection();

            System.out.println("load config ");

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT key,value FROM service_config ");
            ResultSet rs = preparedStatement.executeQuery();


            while (rs.next()) {
                    propertySource.put(rs.getString("key"), rs.getString("value"));
            }
            rs.close();
            preparedStatement.clearParameters();
//            for (int i = 1; i < KEYS.length; i++) {
//
//                String key = KEYS[i];
//
//                preparedStatement.setString(1, key);
//
//                ResultSet rs = preparedStatement.executeQuery();
//
//                // Populate all properties into the property source
//                while (rs.next()) {
//                    propertySource.put(key, rs.getString("value"));
//                }
//
//                rs.close();
//                preparedStatement.clearParameters();
//
//            }

            preparedStatement.close();
            connection.close();

            // Create a custom property source with the highest precedence and add it to Spring Environment
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, propertySource));

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
