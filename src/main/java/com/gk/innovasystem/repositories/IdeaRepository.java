package com.gk.innovasystem.repositories;

import com.gk.innovasystem.entities.IdeaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<IdeaEntity, Long> {
    List<IdeaEntity> findByEventId(Long eventId);
}
