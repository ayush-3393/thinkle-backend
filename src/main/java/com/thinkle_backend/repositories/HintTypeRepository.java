package com.thinkle_backend.repositories;

import com.thinkle_backend.models.HintType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HintTypeRepository extends JpaRepository<HintType, Long> {
    Optional<Boolean> existsByHintTypeIgnoreCase(String hintType);

    Optional<HintType> findByHintTypeIgnoreCase(String hintType);
}
