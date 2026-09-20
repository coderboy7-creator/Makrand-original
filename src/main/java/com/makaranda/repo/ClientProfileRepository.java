package com.makaranda.repo;

import com.makaranda.domain.ClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, Long> {
    List<ClientProfile> findByAstrologerIdOrderByUpdatedAtDesc(Long astrologerId);
    List<ClientProfile> findByOwnerUserIdOrderByUpdatedAtDesc(Long ownerUserId);
}
