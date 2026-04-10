package backend.controller;

import backend.model.Appointment;
import backend.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/client/{email}")
    public List<Appointment> getAppointmentsByClientEmail(@PathVariable String email) {
        return appointmentService.getAppointmentsByClientEmail(email);
    }

    @GetMapping("/provider/{email}")
    public List<Appointment> getAppointmentsByProviderEmail(@PathVariable String email) {
        return appointmentService.getAppointmentsByProviderEmail(email);
    }

    @GetMapping("/provider/{email}/date/{date}")
    public List<Appointment> getAppointmentsByProviderAndDate(@PathVariable String email, @PathVariable String date) {
        return appointmentService.getAppointmentsByProviderAndDate(email, date);
    }

    @GetMapping("/date/{date}")
    public List<Appointment> getAppointmentsByDate(@PathVariable String date) {
        return appointmentService.getAppointmentsByDate(date);
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Appointment appointment) {
        try {
            return ResponseEntity.ok(appointmentService.createAppointment(appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointment) {
        try {
            return ResponseEntity.ok(appointmentService.updateAppointment(id, appointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveAppointment(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(appointmentService.approveAppointment(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @RequestBody(required = false) Map<String, String> payload) {
        try {
            String reason = payload == null ? null : payload.get("reason");
            String actorRole = payload == null ? null : payload.get("actorRole");
            return ResponseEntity.ok(appointmentService.cancelAppointment(id, reason, actorRole));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
