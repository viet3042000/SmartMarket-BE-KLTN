package com.smartmarket.code.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import javax.sql.DataSource;
import java.math.BigDecimal;
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

    /**
     * Adds Spring Environment custom logic. This custom logic fetch properties from database and setting highest precedence
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        Map<String, Object> propertySource = new HashMap<>();
        Map<String, BigDecimal> propertySourceInt = new HashMap<>();

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

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT key,value FROM service_config");
            ResultSet rs = preparedStatement.executeQuery();

            //set properties
            while (rs.next()) {
                if(rs.getString("value") != null ){
                    propertySource.put(rs.getString("key"), rs.getString("value"));
                    continue;
                }
            }
            rs.close();
            preparedStatement.clearParameters();

            preparedStatement.close();
            connection.close();

            // Create a custom property source with the highest precedence and add it to Spring Environment
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, propertySource));

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }finally {

        }
    }
}
