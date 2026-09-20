package com.makaranda.repo;

import com.makaranda.domain.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByAstrologerIdOrderBySlotStartDesc(Long astrologerId);
    List<Consultation> findByClientUserIdOrderBySlotStartDesc(Long clientUserId);
    List<Consultation> findByAstrologerIdAndSlotStartBetween(Long astrologerId, LocalDateTime from, LocalDateTime to);
}
