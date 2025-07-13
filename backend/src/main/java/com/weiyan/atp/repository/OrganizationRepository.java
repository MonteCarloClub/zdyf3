package com.weiyan.atp.repository;

import com.weiyan.atp.data.bean.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 组织数据访问接口
 */
@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {

    /**
     * 根据组织名查找组织
     */
    Optional<OrganizationEntity> findByOrgName(String orgName);

    /**
     * 根据组织类型查找组织列表
     */
    List<OrganizationEntity> findByOrgType(String orgType);

    /**
     * 根据状态查找组织列表
     */
    List<OrganizationEntity> findByStatus(String status);

    /**
     * 检查组织名是否存在
     */
    boolean existsByOrgName(String orgName);

    /**
     * 根据公钥查找组织
     */
    @Query("SELECT o FROM OrganizationEntity o WHERE o.publicKey = :publicKey")
    Optional<OrganizationEntity> findByPublicKey(@Param("publicKey") String publicKey);

    /**
     * 根据关键词模糊查询
     */
    @Query("SELECT o FROM OrganizationEntity o WHERE o.orgName LIKE %:keyword% OR o.displayName LIKE %:keyword%")
    List<OrganizationEntity> findByKeyword(@Param("keyword") String keyword);
}