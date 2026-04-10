package backend.repository;

import backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByClientEmail(String clientEmail);
    List<Appointment> findByDate(String date);
}