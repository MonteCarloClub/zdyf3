package com.weiyan.atp.data.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 组织信息实体类
 * 用于存储组织基本信息和公私钥
 */
@Entity
@Table(name = "atp_organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_name", unique = true, nullable = false, length = 100)
    private String orgName;

    @Column(name = "display_name", length = 200)
    private String displayName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "org_type", length = 50)
    private String orgType;

    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "private_key", columnDefinition = "TEXT")
    private String privateKey;

    @Column(name = "upk", columnDefinition = "TEXT")
    private String upk;

    // DABE相关字段
    @Column(name = "apk_map", columnDefinition = "TEXT")
    private String apkMap;

    @Column(name = "ask_map", columnDefinition = "TEXT")
    private String askMap;

    @Column(name = "opk_map", columnDefinition = "TEXT")
    private String opkMap;

    @Column(name = "osk_map", columnDefinition = "TEXT")
    private String oskMap;

    @Column(name = "egg_alpha", columnDefinition = "TEXT")
    private String eggAlpha;

    @Column(name = "alpha", columnDefinition = "TEXT")
    private String alpha;

    @Column(name = "g_alpha", columnDefinition = "TEXT")
    private String gAlpha;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}