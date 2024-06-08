package com.example.user.global.model.entity;

import com.example.user.global.model.enums.Provider;
import com.example.user.global.model.enums.Role;
import com.example.user.global.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "User")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;    // 고유 번호

    @Column(name = "email", nullable = false, unique = true)
    private String email;   // 사용자 이메일

    @Column(name = "name", nullable = false)
    private String name;    // 사용자 이름

    @Column(name = "profile")
    private String profile; // 프로필 사진 URL

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;      // 사용자 권한

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;  // 사용자 상태 (일시정지, 탈퇴 등)

    @Column(name = "provider", nullable = false)
    private Provider provider;

    public void setStatus(Status status) {
        this.status = status;
    }
}