package backend.service;

import backend.model.Appointment;
import backend.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByClientEmail(String clientEmail) {
        return appointmentRepository.findByClientEmail(clientEmail);
    }

    public List<Appointment> getAppointmentsByDate(String date) {
        return appointmentRepository.findByDate(date);
    }

    public Appointment createAppointment(Appointment appointment) {
        boolean overlapping = appointmentRepository.findByDate(appointment.getDate())
                .stream()
                .anyMatch(existing -> overlaps(
                        existing.getTime24(),
                        existing.getDuration(),
                        appointment.getTime24(),
                        appointment.getDuration()
                ));

        if (overlapping) {
            throw new RuntimeException("Appointment overlaps with an existing booking.");
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment updatedAppointment) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found."));

        boolean overlapping = appointmentRepository.findByDate(updatedAppointment.getDate())
                .stream()
                .anyMatch(existing ->
                        !existing.getId().equals(id) &&
                        overlaps(
                                existing.getTime24(),
                                existing.getDuration(),
                                updatedAppointment.getTime24(),
                                updatedAppointment.getDuration()
                        )
                );

        if (overlapping) {
            throw new RuntimeException("Updated appointment overlaps with an existing booking.");
        }

        existingAppointment.setClientName(updatedAppointment.getClientName());
        existingAppointment.setClientEmail(updatedAppointment.getClientEmail());
        existingAppointment.setDate(updatedAppointment.getDate());
        existingAppointment.setTime24(updatedAppointment.getTime24());
        existingAppointment.setTime(updatedAppointment.getTime());
        existingAppointment.setService(updatedAppointment.getService());
        existingAppointment.setDuration(updatedAppointment.getDuration());

        return appointmentRepository.save(existingAppointment);
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found.");
        }

        appointmentRepository.deleteById(id);
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
}