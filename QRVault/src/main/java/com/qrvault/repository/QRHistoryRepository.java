package com.qrvault.repository;

import com.qrvault.model.QRHistory;
import com.qrvault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QRHistoryRepository extends JpaRepository<QRHistory, Long> {
    List<QRHistory> findByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
}
