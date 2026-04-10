package backend.service;

import backend.model.SystemSetting;
import backend.repository.SystemSettingRepository;
import org.springframework.stereotype.Service;

@Service
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    public SystemSettingService(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    public SystemSetting getSettings() {
        return systemSettingRepository.findById(1L)
                .orElseGet(() -> systemSettingRepository.save(new SystemSetting()));
    }

    public SystemSetting updateSettings(SystemSetting updatedSettings) {
        SystemSetting settings = getSettings();

        Integer cancellationHours = updatedSettings.getCancellationHoursNotice();
        if (cancellationHours == null || cancellationHours < 0) {
            throw new RuntimeException("Cancellation notice hours must be 0 or greater.");
        }

        settings.setCancellationHoursNotice(cancellationHours);
        return systemSettingRepository.save(settings);
    }
}
