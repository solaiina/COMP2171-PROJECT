package backend.repository;

import backend.model.AvailabilityRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, Long> {
    Optional<AvailabilityRule> findByDate(String date);
    void deleteByDate(String date);
    boolean existsByDate(String date);
}