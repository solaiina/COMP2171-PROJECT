package backend.service;

import backend.model.Appointment;
import backend.model.AvailabilityRule;
import backend.model.SystemSetting;
import backend.model.User;
import backend.repository.AppointmentRepository;
import backend.repository.ServiceItemRepository;
import backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final UserRepository userRepository;
    private final AvailabilityRuleService availabilityRuleService;
    private final SystemSettingService systemSettingService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ServiceItemRepository serviceItemRepository,
            UserRepository userRepository,
            AvailabilityRuleService availabilityRuleService,
            SystemSettingService systemSettingService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.userRepository = userRepository;
        this.availabilityRuleService = availabilityRuleService;
        this.systemSettingService = systemSettingService;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllByOrderByDateAscTime24Asc();
    }

    public List<Appointment> getAppointmentsByClientEmail(String clientEmail) {
        return appointmentRepository.findByClientEmailOrderByDateAscTime24Asc(clientEmail);
    }

    public List<Appointment> getAppointmentsByProviderEmail(String providerEmail) {
        return appointmentRepository.findByProviderEmailOrderByDateAscTime24Asc(providerEmail);
    }

    public List<Appointment> getAppointmentsByProviderAndDate(String providerEmail, String date) {
        return appointmentRepository.findByProviderEmailAndDateOrderByTime24Asc(providerEmail, date);
    }

    public List<Appointment> getAppointmentsByDate(String date) {
        return appointmentRepository.findByDateOrderByTime24Asc(date);
    }

    public Appointment createAppointment(Appointment appointment) {
        validateBaseAppointment(appointment);
        populateProviderName(appointment);
        appointment.setStatus("PENDING");
        appointment.setCancellationReason(null);

        validateServiceOwnership(appointment);
        validateAgainstAvailability(appointment);
        validateNoOverlap(null, appointment);

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment updatedAppointment) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found."));

        if ("CANCELLED".equalsIgnoreCase(existingAppointment.getStatus())) {
            throw new RuntimeException("Cancelled appointments cannot be updated.");
        }

        validateBaseAppointment(updatedAppointment);
        populateProviderName(updatedAppointment);
        validateServiceOwnership(updatedAppointment);
        validateAgainstAvailability(updatedAppointment);
        validateNoOverlap(id, updatedAppointment);

        String previousSlotKey = buildSlotKey(existingAppointment.getDate(), existingAppointment.getTime24());

        existingAppointment.setClientName(updatedAppointment.getClientName());
        existingAppointment.setClientEmail(updatedAppointment.getClientEmail());
        existingAppointment.setProviderName(updatedAppointment.getProviderName());
        existingAppointment.setProviderEmail(updatedAppointment.getProviderEmail());
        existingAppointment.setDate(updatedAppointment.getDate());
        existingAppointment.setTime24(updatedAppointment.getTime24());
        existingAppointment.setTime(updatedAppointment.getTime());
        existingAppointment.setService(updatedAppointment.getService());
        existingAppointment.setDuration(updatedAppointment.getDuration());
        existingAppointment.setCancellationReason(null);

        String newSlotKey = buildSlotKey(updatedAppointment.getDate(), updatedAppointment.getTime24());

        if (!previousSlotKey.equals(newSlotKey) && "APPROVED".equalsIgnoreCase(existingAppointment.getStatus())) {
            existingAppointment.setStatus("PENDING");
        } else if (updatedAppointment.getStatus() != null && !updatedAppointment.getStatus().isBlank()) {
            existingAppointment.setStatus(updatedAppointment.getStatus().trim().toUpperCase());
        }

        return appointmentRepository.save(existingAppointment);
    }

    public Appointment approveAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found."));

        if ("CANCELLED".equalsIgnoreCase(appointment.getStatus())) {
            throw new RuntimeException("Cancelled appointments cannot be approved.");
        }

        appointment.setStatus("APPROVED");
        return appointmentRepository.save(appointment);
    }

    public Appointment cancelAppointment(Long id, String reason, String actorRole) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found."));

        if ("CANCELLED".equalsIgnoreCase(appointment.getStatus())) {
            return appointment;
        }

        validateCancellationWindow(appointment, actorRole);
        appointment.setStatus("CANCELLED");
        appointment.setCancellationReason(reason == null || reason.isBlank() ? null : reason.trim());

        return appointmentRepository.save(appointment);
    }

    private void validateBaseAppointment(Appointment appointment) {
        if (appointment.getClientName() == null || appointment.getClientName().isBlank()) {
            throw new RuntimeException("Client name is required.");
        }

        if (appointment.getClientEmail() == null || appointment.getClientEmail().isBlank()) {
            throw new RuntimeException("Client email is required.");
        }

        if (appointment.getProviderEmail() == null || appointment.getProviderEmail().isBlank()) {
            throw new RuntimeException("Provider email is required.");
        }

        if (appointment.getDate() == null || appointment.getDate().isBlank()) {
            throw new RuntimeException("Appointment date is required.");
        }

        if (appointment.getTime24() == null || appointment.getTime24().isBlank()) {
            throw new RuntimeException("Appointment time is required.");
        }

        if (appointment.getTime() == null || appointment.getTime().isBlank()) {
            throw new RuntimeException("Display time is required.");
        }

        if (appointment.getService() == null || appointment.getService().isBlank()) {
            throw new RuntimeException("Service is required.");
        }

        if (appointment.getDuration() == null || appointment.getDuration() <= 0) {
            throw new RuntimeException("Appointment duration must be greater than 0.");
        }

        LocalDate.parse(appointment.getDate());
        timeToMinutes(appointment.getTime24());
    }

    private void populateProviderName(Appointment appointment) {
        if (appointment.getProviderName() == null || appointment.getProviderName().isBlank()) {
            User provider = userRepository.findByEmail(appointment.getProviderEmail())
                    .orElseThrow(() -> new RuntimeException("Provider not found."));

            appointment.setProviderName(provider.getFirstName() + " " + provider.getLastName());
        }
    }

    private void validateServiceOwnership(Appointment appointment) {
        boolean valid = serviceItemRepository.findByProviderEmailOrderByNameAsc(appointment.getProviderEmail())
                .stream()
                .anyMatch(serviceItem ->
                        serviceItem.getName().equalsIgnoreCase(appointment.getService())
                                && serviceItem.getDuration().equals(appointment.getDuration())
                );

        if (!valid) {
            throw new RuntimeException("That service is not available for the selected provider.");
        }
    }

    private void validateAgainstAvailability(Appointment appointment) {
        AvailabilityRule effectiveRule;

        try {
            effectiveRule = availabilityRuleService.getRuleByProviderAndDate(
                    appointment.getProviderEmail(),
                    appointment.getDate()
            );
        } catch (RuntimeException ex) {
            effectiveRule = null;
        }

        String status = effectiveRule == null ? "available" : effectiveRule.getStatus();

        if ("blocked".equalsIgnoreCase(status)) {
            throw new RuntimeException("That date is unavailable for the selected provider.");
        }

        int start = timeToMinutes(appointment.getTime24());
        int end = start + appointment.getDuration();

        String allowedStartText = "09:00";
        String allowedEndText = "17:00";

        if (effectiveRule != null && effectiveRule.getStartTime() != null && effectiveRule.getEndTime() != null) {
            allowedStartText = effectiveRule.getStartTime();
            allowedEndText = effectiveRule.getEndTime();
        }

        int allowedStart = timeToMinutes(allowedStartText);
        int allowedEnd = timeToMinutes(allowedEndText);

        if (start < allowedStart || end > allowedEnd) {
            throw new RuntimeException("The selected time is outside the provider's available hours.");
        }
    }

    private void validateNoOverlap(Long idToIgnore, Appointment targetAppointment) {
        boolean overlapping = appointmentRepository.findByProviderEmailAndDateOrderByTime24Asc(
                        targetAppointment.getProviderEmail(),
                        targetAppointment.getDate()
                )
                .stream()
                .filter(existing -> idToIgnore == null || !existing.getId().equals(idToIgnore))
                .filter(existing -> !"CANCELLED".equalsIgnoreCase(existing.getStatus()))
                .anyMatch(existing -> overlaps(
                        existing.getTime24(),
                        existing.getDuration(),
                        targetAppointment.getTime24(),
                        targetAppointment.getDuration()
                ));

        if (overlapping) {
            throw new RuntimeException("Appointment overlaps with an existing booking.");
        }
    }

    private void validateCancellationWindow(Appointment appointment, String actorRole) {
        if ("provider".equalsIgnoreCase(actorRole) || "admin".equalsIgnoreCase(actorRole)) {
            return;
        }

        SystemSetting settings = systemSettingService.getSettings();
        int requiredNoticeHours = settings.getCancellationHoursNotice() == null
                ? 24
                : settings.getCancellationHoursNotice();

        LocalDateTime appointmentDateTime = LocalDateTime.parse(appointment.getDate() + "T" + appointment.getTime24());
        long hoursUntilAppointment = Duration.between(LocalDateTime.now(), appointmentDateTime).toHours();

        if (hoursUntilAppointment < requiredNoticeHours) {
            throw new RuntimeException(
                    "Cancellation is not allowed within " + requiredNoticeHours + " hours of the appointment."
            );
        }
    }

    private boolean overlaps(String startA, Integer durationA, String startB, Integer durationB) {
        int aStart = timeToMinutes(startA);
        int aEnd = aStart + durationA;

        int bStart = timeToMinutes(startB);
        int bEnd = bStart + durationB;

        return aStart < bEnd && bStart < aEnd;
    }

    private int timeToMinutes(String time24) {
        String[] parts = time24.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return hour * 60 + minute;
    }

    private String buildSlotKey(String date, String time24) {
        return date + "|" + time24;
    }
}
