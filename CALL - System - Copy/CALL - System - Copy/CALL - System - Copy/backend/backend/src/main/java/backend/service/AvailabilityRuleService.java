package backend.service;

import backend.model.AvailabilityRule;
import backend.repository.AvailabilityRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvailabilityRuleService {

    private final AvailabilityRuleRepository availabilityRuleRepository;

    public AvailabilityRuleService(AvailabilityRuleRepository availabilityRuleRepository) {
        this.availabilityRuleRepository = availabilityRuleRepository;
    }

    public List<AvailabilityRule> getAllRules() {
        return availabilityRuleRepository.findAll();
    }

    public AvailabilityRule saveRule(AvailabilityRule rule) {
        return availabilityRuleRepository.findByDate(rule.getDate())
                .map(existingRule -> {
                    existingRule.setStatus(rule.getStatus());
                    existingRule.setStartTime(rule.getStartTime());
                    existingRule.setEndTime(rule.getEndTime());
                    return availabilityRuleRepository.save(existingRule);
                })
                .orElseGet(() -> availabilityRuleRepository.save(rule));
    }

    public AvailabilityRule getRuleByDate(String date) {
        return availabilityRuleRepository.findByDate(date)
                .orElseThrow(() -> new RuntimeException("Availability rule not found."));
    }

    public void deleteRuleByDate(String date) {
        if (!availabilityRuleRepository.existsByDate(date)) {
            throw new RuntimeException("Availability rule not found.");
        }

        availabilityRuleRepository.deleteByDate(date);
    }
}