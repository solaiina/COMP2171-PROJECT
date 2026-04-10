package backend.repository;

import backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByDateAscTime24Asc();
    List<Appointment> findByClientEmailOrderByDateAscTime24Asc(String clientEmail);
    List<Appointment> findByProviderEmailOrderByDateAscTime24Asc(String providerEmail);
    List<Appointment> findByProviderEmailAndDateOrderByTime24Asc(String providerEmail, String date);
    List<Appointment> findByDateOrderByTime24Asc(String date);
}
