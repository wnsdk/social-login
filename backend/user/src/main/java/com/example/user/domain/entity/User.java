package com.example.user.domain.entity;

import com.example.user.domain.enums.Role;
import com.example.user.domain.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;    // 고유 번호

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

}
