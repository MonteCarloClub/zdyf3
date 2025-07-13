package com.weiyan.atp.data.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户信息实体类
 * 用于存储用户基本信息和公私钥
 */
@Entity
@Table(name = "atp_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", unique = true, nullable = false, length = 100)
    private String userName;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "user_type", length = 50)
    private String userType;

    @Column(name = "channel", length = 100)
    private String channel;

    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "private_key", columnDefinition = "TEXT")
    private String privateKey;

    @Column(name = "upk", columnDefinition = "TEXT")
    private String upk;

    // DABE相关字段，存储为JSON字符串
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

    @Column(name = "applied_attr_map", columnDefinition = "TEXT")
    private String appliedAttrMap;

    @Column(name = "privacy_attr_map", columnDefinition = "TEXT")
    private String privacyAttrMap;

    @Column(name = "expire_date", length = 50)
    private String expireDate;

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
