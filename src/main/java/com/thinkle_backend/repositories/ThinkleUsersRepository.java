package com.thinkle_backend.repositories;

import com.thinkle_backend.models.ThinkleUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThinkleUsersRepository extends JpaRepository<ThinkleUsers, Long> {
}
