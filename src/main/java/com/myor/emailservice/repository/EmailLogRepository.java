package com.myor.emailservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myor.emailservice.entity.EmailLog;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
}
