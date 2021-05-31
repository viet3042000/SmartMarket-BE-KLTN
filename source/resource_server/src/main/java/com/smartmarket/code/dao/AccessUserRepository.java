package com.smartmarket.code.dao;

import com.smartmarket.code.model.AccessUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface AccessUserRepository extends JpaRepository<AccessUser,Long> {

    @Query(value = "select au from AccessUser au " +
            "left join UserUrl uu on uu.id = au.usersUrlsId " +
            "left join Url ur on ur.id = uu.urlId " +
            "left join User u on u.id = uu.userId " +
            "where ur.id =:urlId and u.id = :userIdToken ")
    public Set<AccessUser> findAccessUserByUserIdAndUserUrlId(@Param("userIdToken") Long userIdToken, @Param("urlId") Long urlId);




    @Query(value = "select au from AccessUser au " +
            "left join UserUrl uu on uu.id = au.usersUrlsId " +
            "left join Url ur on ur.id = uu.urlId " +
            "left join User u on u.id = uu.userId " +
            "where ur.id =:urlId and u.id = :userIdToken and  au.userId = :userIdAccess")
    public Set<AccessUser> checkAccessUser(@Param("userIdToken") Long userIdToken, @Param("urlId") Long urlId , @Param("userIdAccess") Long userIdAccess);


//	@Query(value = "from User u where u.username =:username")
//	public Optional<User> findByUsername(@Param("username") String username);

}