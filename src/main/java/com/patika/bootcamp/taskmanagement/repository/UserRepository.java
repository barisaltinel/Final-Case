package com.patika.bootcamp.taskmanagement.repository;

import com.patika.bootcamp.taskmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // ✅ Soft delete desteği olan kullanıcıları listeleme
    List<User> findAllByDeletedFalse();

    // ✅ Soft delete desteği ile ID'ye göre kullanıcı bulma
    Optional<User> findByIdAndDeletedFalse(Long id);
}
