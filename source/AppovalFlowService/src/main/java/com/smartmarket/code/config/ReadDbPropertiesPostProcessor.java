package com.smartmarket.code.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReadDbPropertiesPostProcessor implements EnvironmentPostProcessor {
    /**
     * Name of the custom property source added by this post processor class
     */
    final String PROPERTY_SOURCE_NAME = "databaseProperties";

    /**
     * Adds Spring Environment custom logic. This custom logic fetch properties from database and setting highest precedence
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        HikariConfig config = new HikariConfig();
        HikariDataSource ds = null ;
        Map<String, Object> propertySource = new HashMap<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            // Build manually datasource to ServiceConfig
            config.setJdbcUrl(environment.getProperty("spring.datasource.url"));
            config.setUsername(environment.getProperty("spring.datasource.username"));
            config.setPassword(environment.getProperty("spring.datasource.password"));
            config.setDriverClassName("org.postgresql.Driver");
            config.setIdleTimeout(10000);
            config.setMaxLifetime(20000);
            config.setMaximumPoolSize(1);
            ds = new HikariDataSource(config);

//            DataSource ds = DataSourceBuilder
//                    .create()
//                    .username(environment.getProperty("spring.datasource.username"))
//                    .password(environment.getProperty("spring.datasource.password"))
//                    .url(environment.getProperty("spring.datasource.url"))
//                    .driverClassName("org.postgresql.Driver")
//                    .build();
            // Fetch all properties
            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement("SELECT key,value FROM service_config");
            rs = preparedStatement.executeQuery();

            //set properties
            while (rs.next()) {
                if(rs.getString("value") != null ){
                    propertySource.put(rs.getString("key"), rs.getString("value"));
                    continue;
                }else{
                    propertySource.put(rs.getString("key"), "");
                    continue;
                }
            }
            rs.close();
            preparedStatement.clearParameters();
            preparedStatement.close();
            connection.close();
            ds.close();

            // Create a custom property source with the highest precedence and add it to Spring Environment
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, propertySource));

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }finally {
            try {

                if (rs != null) {
                    rs.close();
                }

                if (preparedStatement != null) {
                    preparedStatement.close();
                }

                if (connection != null) {
                    connection.close();
                }
                if (ds != null) {
                    ds.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
    }
}
