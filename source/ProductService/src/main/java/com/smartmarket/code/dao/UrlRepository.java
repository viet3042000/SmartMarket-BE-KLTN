package com.smartmarket.code.dao;


import com.smartmarket.code.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    @Query(value = "from Url u join ClientUrl cu on cu.urlName = u.urlName where cu.clientIdName = :clientIdName  ")
    public Set<Url> findUrlByClientNameActive(@Param("clientIdName") String clientIdName);

//    @Query(value = "from Url u join ClientUrl cu on cu.urlId = u.id where cu.clientId = :clientId  ")
//    public Set<Url> findUrlByClientIdActive(@Param("clientId") Long clientId);

    @Query(value = "from Url u join UserUrl uu on uu.urlId = u.id where uu.userId = :userId  ")
    public Set<Url> findUrlByUserIdActive(@Param("userId") Long userId);

}