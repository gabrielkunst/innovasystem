package com.gk.innovasystem.repositories;

import com.gk.innovasystem.entities.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
    boolean existsByUserIdAndIdeaId(Long userId, Long ideaId);
    int countByIdeaId(Long id);
}
