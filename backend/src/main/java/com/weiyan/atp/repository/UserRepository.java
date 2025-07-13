package com.weiyan.atp.repository;

import com.weiyan.atp.data.bean.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * 根据用户名查找用户
     */
    Optional<UserEntity> findByUserName(String userName);

    /**
     * 根据用户名和密码查找用户
     */
    Optional<UserEntity> findByUserNameAndPassword(String userName, String password);

    /**
     * 根据用户类型查找用户列表
     */
    List<UserEntity> findByUserType(String userType);

    /**
     * 根据渠道查找用户列表
     */
    List<UserEntity> findByChannel(String channel);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUserName(String userName);

    /**
     * 根据公钥查找用户
     */
    @Query("SELECT u FROM UserEntity u WHERE u.publicKey = :publicKey")
    Optional<UserEntity> findByPublicKey(@Param("publicKey") String publicKey);

    /**
     * 根据用户名模糊查询
     */
    @Query("SELECT u FROM UserEntity u WHERE u.userName LIKE %:keyword% OR u.name LIKE %:keyword%")
    List<UserEntity> findByKeyword(@Param("keyword") String keyword);
}
