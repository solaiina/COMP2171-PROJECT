package backend.controller;

import backend.model.AvailabilityRule;
import backend.service.AvailabilityRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/availability")
@CrossOrigin(origins = "*")
public class AvailabilityRuleController {

    private final AvailabilityRuleService availabilityRuleService;

    public AvailabilityRuleController(AvailabilityRuleService availabilityRuleService) {
        this.availabilityRuleService = availabilityRuleService;
    }

    @GetMapping
    public List<AvailabilityRule> getAllRules() {
        return availabilityRuleService.getAllRules();
    }

    @GetMapping("/provider/{email}")
    public List<AvailabilityRule> getRulesByProvider(@PathVariable String email) {
        return availabilityRuleService.getRulesByProvider(email);
    }

    @GetMapping("/provider/{email}/date/{date}")
    public ResponseEntity<?> getRuleByProviderAndDate(@PathVariable String email, @PathVariable String date) {
        try {
            return ResponseEntity.ok(availabilityRuleService.getRuleByProviderAndDate(email, date));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> saveRule(@RequestBody AvailabilityRule rule) {
        try {
            return ResponseEntity.ok(availabilityRuleService.saveRule(rule));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable Long id) {
        try {
            availabilityRuleService.deleteRule(id);
            return ResponseEntity.ok("Availability rule deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
