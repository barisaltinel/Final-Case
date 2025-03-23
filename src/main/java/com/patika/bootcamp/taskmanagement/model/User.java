package com.patika.bootcamp.taskmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Column(nullable = false)
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be empty")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Role cannot be empty")
    @Column(nullable = false)
    private String role; // ✅ Kullanıcı rolü eklendi (ADMIN, PROJECT_MANAGER, TEAM_LEADER, TEAM_MEMBER)

    @Column(nullable = false)
    private boolean deleted = false; // ✅ Soft delete desteği

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // ✅ Kullanıcı oluşturulma tarihi

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // ✅ Otomatik oluşturma tarihi ekleniyor
    }

    /** ✅ Kullanıcıyı pasif olarak silmek için yardımcı metod */
    public void softDelete() {
        this.deleted = true;
    }
}
