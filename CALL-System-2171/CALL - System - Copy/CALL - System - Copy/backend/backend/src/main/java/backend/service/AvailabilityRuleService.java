package backend.service;

import backend.model.AvailabilityRule;
import backend.repository.AvailabilityRuleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityRuleService {

    private final AvailabilityRuleRepository availabilityRuleRepository;

    public AvailabilityRuleService(AvailabilityRuleRepository availabilityRuleRepository) {
        this.availabilityRuleRepository = availabilityRuleRepository;
    }

    public List<AvailabilityRule> getAllRules() {
        return availabilityRuleRepository.findAllByOrderByProviderEmailAscRecurrenceTypeAscDateAscDayOfWeekAsc();
    }

    public List<AvailabilityRule> getRulesByProvider(String providerEmail) {
        return availabilityRuleRepository.findByProviderEmailOrderByRecurrenceTypeAscDateAscDayOfWeekAsc(providerEmail);
    }

    public AvailabilityRule saveRule(AvailabilityRule rule) {
        normalizeRule(rule);
        validateRule(rule);

        AvailabilityRule existingRule = findExistingRule(rule);

        if (existingRule != null) {
            existingRule.setProviderEmail(rule.getProviderEmail());
            existingRule.setDate(rule.getDate());
            existingRule.setStatus(rule.getStatus());
            existingRule.setStartTime(rule.getStartTime());
            existingRule.setEndTime(rule.getEndTime());
            existingRule.setRecurrenceType(rule.getRecurrenceType());
            existingRule.setDayOfWeek(rule.getDayOfWeek());
            existingRule.setReason(rule.getReason());
            return availabilityRuleRepository.save(existingRule);
        }

        return availabilityRuleRepository.save(rule);
    }

    public AvailabilityRule getRuleByProviderAndDate(String providerEmail, String date) {
        AvailabilityRule specific = availabilityRuleRepository.findByProviderEmailAndDate(providerEmail, date)
                .orElse(null);

        if (specific != null) {
            return specific;
        }

        String dayOfWeek = LocalDate.parse(date).getDayOfWeek().name();

        return availabilityRuleRepository.findByProviderEmailAndRecurrenceTypeAndDayOfWeek(
                        providerEmail,
                        "weekly",
                        dayOfWeek
                )
                .orElseThrow(() -> new RuntimeException("Availability rule not found."));
    }

    public void deleteRule(Long id) {
        if (!availabilityRuleRepository.existsById(id)) {
            throw new RuntimeException("Availability rule not found.");
        }

        availabilityRuleRepository.deleteById(id);
    }

    private void normalizeRule(AvailabilityRule rule) {
        String recurrenceType = rule.getRecurrenceType();

        if (recurrenceType == null || recurrenceType.isBlank()) {
            recurrenceType = "single";
        }

        recurrenceType = recurrenceType.trim().toLowerCase();
        rule.setRecurrenceType(recurrenceType);

        if (rule.getStatus() != null) {
            rule.setStatus(rule.getStatus().trim().toLowerCase());
        }

        if (rule.getProviderEmail() != null) {
            rule.setProviderEmail(rule.getProviderEmail().trim().toLowerCase());
        }

        if ("single".equals(recurrenceType)) {
            rule.setDayOfWeek(null);
        } else if ("weekly".equals(recurrenceType) && rule.getDayOfWeek() != null) {
            rule.setDayOfWeek(rule.getDayOfWeek().trim().toUpperCase());
        }
    }

    private void validateRule(AvailabilityRule rule) {
        if (rule.getProviderEmail() == null || rule.getProviderEmail().isBlank()) {
            throw new RuntimeException("Provider email is required.");
        }

        if (rule.getStatus() == null || rule.getStatus().isBlank()) {
            throw new RuntimeException("Availability status is required.");
        }

        if (!"single".equals(rule.getRecurrenceType()) && !"weekly".equals(rule.getRecurrenceType())) {
            throw new RuntimeException("Recurrence type must be single or weekly.");
        }

        if ("single".equals(rule.getRecurrenceType())) {
            if (rule.getDate() == null || rule.getDate().isBlank()) {
                throw new RuntimeException("A date is required for a single-day rule.");
            }
            LocalDate.parse(rule.getDate());
        }

        if ("weekly".equals(rule.getRecurrenceType())) {
            if (rule.getDayOfWeek() == null || rule.getDayOfWeek().isBlank()) {
                throw new RuntimeException("A day of week is required for a weekly rule.");
            }
            DayOfWeek.valueOf(rule.getDayOfWeek());
            rule.setDate(null);
        }

        if ("partial".equals(rule.getStatus()) || "available".equals(rule.getStatus())) {
            if (rule.getStartTime() != null && rule.getEndTime() != null && rule.getStartTime().compareTo(rule.getEndTime()) >= 0) {
                throw new RuntimeException("End time must be after start time.");
            }
        }

        if (!"partial".equals(rule.getStatus())) {
            if ((rule.getStartTime() == null || rule.getStartTime().isBlank())
                    || (rule.getEndTime() == null || rule.getEndTime().isBlank())) {
                // keep full-day available/blocked rules simple by clearing both
                rule.setStartTime(null);
                rule.setEndTime(null);
            }
        }
    }

    private AvailabilityRule findExistingRule(AvailabilityRule rule) {
        if (rule.getId() != null) {
            return availabilityRuleRepository.findById(rule.getId()).orElse(null);
        }

        if ("single".equals(rule.getRecurrenceType())) {
            return availabilityRuleRepository.findByProviderEmailAndDate(rule.getProviderEmail(), rule.getDate())
                    .orElse(null);
        }

        return availabilityRuleRepository.findByProviderEmailAndRecurrenceTypeAndDayOfWeek(
                        rule.getProviderEmail(),
                        rule.getRecurrenceType(),
                        rule.getDayOfWeek()
                )
                .orElse(null);
    }
}
