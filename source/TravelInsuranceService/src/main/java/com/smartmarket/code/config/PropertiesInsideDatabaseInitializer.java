//package com.smartmarket.code.config;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.PropertyEditorRegistry;
//import org.springframework.beans.factory.BeanCreationException;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.context.properties.ConfigurationPropertiesBean;
//import org.springframework.boot.context.properties.bind.BindHandler;
//import org.springframework.boot.context.properties.bind.Bindable;
//import org.springframework.boot.context.properties.bind.Binder;
//import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
//import org.springframework.boot.context.properties.bind.handler.IgnoreTopLevelConverterNotFoundBindHandler;
//import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
//import org.springframework.boot.convert.ApplicationConversionService;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.core.env.MapPropertySource;
//import org.springframework.core.env.MutablePropertySources;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.util.Assert;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Consumer;
//
//public class PropertiesInsideDatabaseInitializer implements BeanPostProcessor, InitializingBean, ApplicationContextAware {
//    private JdbcTemplate jdbcTemplate;
//    private ApplicationContext applicationContext;
//    private BeanDefinitionRegistry registry;
//    private Map<String, Object> systemConfigMap = new HashMap<>();
//
//    private final String propertySourceName = "propertiesInsideDatabase";
//
//    public PropertiesInsideDatabaseInitializer(JdbcTemplate jdbcTemplate){
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        bind(ConfigurationPropertiesBean.get(this.applicationContext, bean, beanName));
//        return bean;
//    }
//
//    private void bind(ConfigurationPropertiesBean propertiesBean) {
//        if (propertiesBean == null || hasBoundValueObject(propertiesBean.getName())) {
//            return;
//        }
//        Assert.state(propertiesBean.getBindMethod() == ConfigurationPropertiesBean.BindMethod.JAVA_BEAN, "Cannot bind @ConfigurationProperties for bean '"
//                + propertiesBean.getName() + "'. Ensure that @ConstructorBinding has not been applied to regular bean");
//        try {
//            Bindable<?> target = propertiesBean.asBindTarget();
//            ConfigurationProperties annotation = propertiesBean.getAnnotation();
//            BindHandler bindHandler = new IgnoreTopLevelConverterNotFoundBindHandler();
//            MutablePropertySources mutablePropertySources = new MutablePropertySources();
//            mutablePropertySources.addLast(new MapPropertySource(propertySourceName, systemConfigMap));
//            Binder binder = new Binder(ConfigurationPropertySources.from(mutablePropertySources), new PropertySourcesPlaceholdersResolver(mutablePropertySources),
//                    ApplicationConversionService.getSharedInstance(), getPropertyEditorInitializer(), null);
//            binder.bind(annotation.prefix(), target, bindHandler);
//        }
//        catch (Exception ex) {
//            throw new BeanCreationException("", ex);
//        }
//    }
//
//    private Consumer<PropertyEditorRegistry> getPropertyEditorInitializer() {
//        if (this.applicationContext instanceof ConfigurableApplicationContext) {
//            return ((ConfigurableApplicationContext) this.applicationContext).getBeanFactory()::copyRegisteredEditorsTo;
//        }
//        return null;
//    }
//
//    private boolean hasBoundValueObject(String beanName) {
//        return this.registry.containsBeanDefinition(beanName) && this.registry
//                .getBeanDefinition(beanName).getClass().getName().contains("ConfigurationPropertiesValueObjectBeanDefinition");
//    }
//
//    @Override
//    public void afterPropertiesSet() {
//        String sql = "SELECT key, value from system_config";
//        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
//        for (Map<String, Object> map : maps) {
//            String key = String.valueOf(map.get("key"));
//            Object value = map.get("value");
//            systemConfigMap.put(key, value);
//        }
//        this.registry = (BeanDefinitionRegistry) this.applicationContext.getAutowireCapableBeanFactory();
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//}