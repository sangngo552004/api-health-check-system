package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<RefreshTokenJpaEntity> findByToken(String token);

  @Modifying
  @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.token = :token")
  void deleteByToken(@Param("token") String token);

  @Modifying
  @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.user.id = :userId")
  void deleteByUserId(@Param("userId") Long userId);
}
