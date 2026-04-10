package backend.repository;

import backend.model.AvailabilityRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, Long> {
    List<AvailabilityRule> findAllByOrderByProviderEmailAscRecurrenceTypeAscDateAscDayOfWeekAsc();
    List<AvailabilityRule> findByProviderEmailOrderByRecurrenceTypeAscDateAscDayOfWeekAsc(String providerEmail);
    Optional<AvailabilityRule> findByProviderEmailAndDate(String providerEmail, String date);
    Optional<AvailabilityRule> findByProviderEmailAndRecurrenceTypeAndDayOfWeek(
            String providerEmail,
            String recurrenceType,
            String dayOfWeek
    );
}
