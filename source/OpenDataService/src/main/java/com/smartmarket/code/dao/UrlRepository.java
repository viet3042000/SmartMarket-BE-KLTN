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
    public Set<Url> findUrlByClientIdActive(@Param("clientIdName") String clientIdName);

    @Query(value = "from Url u join UserUrl uu on uu.urlName = u.urlName where uu.userName = :userName  ")
    public Set<Url> findUrlByUserIdActive(@Param("userName") String userName);

}