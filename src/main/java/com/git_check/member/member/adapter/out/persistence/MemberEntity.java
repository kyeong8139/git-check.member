package com.git_check.member.member.adapter.out.persistence;

import jakarta.persistence.Entity;  
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

import com.git_check.member.member.application.domain.Member;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "social_login_type", nullable = false)
    private String socialLoginType;

    @Column(name = "social_login_id", nullable = false)
    private String socialLoginId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public MemberEntity(Long id, String name, String socialLoginType, String socialLoginId) {
        this.id = id;
        this.name = name;
        this.socialLoginType = socialLoginType;
        this.socialLoginId = socialLoginId;
    }

    public Member toModel() {
        return Member.builder()
            .id(id)
            .name(name)
            .socialLoginType(socialLoginType)
            .socialLoginId(socialLoginId)
            .build();
    }

    public static MemberEntity from(Member member) {
        return MemberEntity.builder()
            .id(member.getId())
            .name(member.getName())
            .socialLoginType(member.getSocialLoginType())
            .socialLoginId(member.getSocialLoginId())
            .build();
    }
}
