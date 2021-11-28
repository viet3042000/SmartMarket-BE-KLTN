package com.smartmarket.code.dao;


import com.smartmarket.code.model.UserProductProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface UserProductProviderRepository extends JpaRepository<UserProductProvider, String> {
    @Query(value = "Select * FROM user_product_provider where user_name =:user_name", nativeQuery = true)
    public Optional<UserProductProvider> findByUserName(@Param("user_name") String userName) ;

    @Query(value = "Select * FROM user_product_provider where user_name =:user_name " +
            "and product_provider_id =:product_provider_id", nativeQuery = true)
    public Optional<UserProductProvider> findUser(@Param("user_name") String userName, @Param("product_provider_id") Long productProviderId) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM user_product_provider where user_name =:user_name", nativeQuery = true)
    public int deleteUserProductProviderKafka(@Param("user_name") String userName) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "TRUNCATE TABLE user_product_provider",  nativeQuery = true)
    public int truncateUserProductProviderKafka() ;
}
