//package com.example.authserver.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Transactional
//@Repository
//public interface RoleRepository extends JpaRepository<Role, String> {
//
//    @Query(value = "from Role u where u.roleName =:roleName")
//    public Optional<Role> findByUserRoleName(@Param("roleName") String roleName);
//
//    @Modifying(clearAutomatically = true)
//    @Query(value = "DELETE FROM roles where role_name =:role_name", nativeQuery = true)
//    public int deleteRoleKafka(@Param("role_name") String roleName) ;
//
//    @Modifying(clearAutomatically = true)
//    @Query(value = "TRUNCATE TABLE roles",  nativeQuery = true)
//    public int truncateRoleKafka() ;
//
//}