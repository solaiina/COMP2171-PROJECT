package backend.controller;

import backend.model.SystemSetting;
import backend.service.SystemSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@CrossOrigin(origins = "*")
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    public SystemSettingController(SystemSettingService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @GetMapping
    public SystemSetting getSettings() {
        return systemSettingService.getSettings();
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(@RequestBody SystemSetting settings) {
        try {
            return ResponseEntity.ok(systemSettingService.updateSettings(settings));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
