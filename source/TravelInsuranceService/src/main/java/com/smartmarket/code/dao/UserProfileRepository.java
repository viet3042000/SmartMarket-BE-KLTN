package com.smartmarket.code.dao;

import com.smartmarket.code.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // Đây là JPQL (Hibernate) --> dùng tên biến của User
    @Query(value = "from UserProfile u where u.userName =:userName")
    public Optional<UserProfile> findByUsername(@Param("userName") String userName);


    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE user_profile set user_name =:user_name, email =:email, gender=:gender," +
            " phone_number=:phone_number, address=:address, identify_number=:identify_number," +
            " birth_date=:birth_date, full_name=:full_name," +
            " enabled=:enabled where id = :id",nativeQuery = true)
    public int updateUserProfileKafka(@Param("user_name") String userName,@Param("email") String email, @Param("gender") Long gender,
                                      @Param("enabled") Long enabled,@Param("id") Long id,@Param("phone_number") String phoneNumber,
                                      @Param("address") String address, @Param("identify_number") String identifyNumber,
                                      @Param("birth_date") String birthDate,@Param("full_name") String fullName) ;


    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM user_profile where id =:id", nativeQuery = true)
    public int deleteUserProfileById(@Param("id") Long id) ;

    @Modifying(clearAutomatically = true)
    @Query(value = "TRUNCATE TABLE user_profile",  nativeQuery = true)
    public int truncateUserProfileKafka() ;

}