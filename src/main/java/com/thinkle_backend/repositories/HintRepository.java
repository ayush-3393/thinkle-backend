package com.thinkle_backend.repositories;

import com.thinkle_backend.models.Hint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HintRepository extends JpaRepository<Hint, Long> {
    Long countByGameSession_User_IdAndGameSession_GameDate(Long userId, LocalDate gameDate);
    boolean existsByGameSession_IdAndWordHint_HintType_Id(Long gameSessionId, Long hintTypeId);
    List<Hint> findByGameSession_User_IdAndGameSession_GameDate(Long userId, LocalDate gameDate);

}
