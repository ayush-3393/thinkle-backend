package com.thinkle_backend.repositories;

import com.thinkle_backend.models.Hint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface HintRepository extends JpaRepository<Hint, Long> {
    Long countByGameSession_User_IdAndGameSession_GameDate(Long userId, LocalDate gameDate);
    boolean existsByGameSession_IdAndWordHint_HintType_Id(Long gameSessionId, Long hintTypeId);

}
