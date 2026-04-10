package backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientName;
    private String clientEmail;
    private String date;
    private String time24;
    private String time;
    private String service;
    private Integer duration;

    public Appointment() {
    }

    public Appointment(Long id, String clientName, String clientEmail, String date,
                       String time24, String time, String service, Integer duration) {
        this.id = id;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.date = date;
        this.time24 = time24;
        this.time = time;
        this.service = service;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime24() {
        return time24;
    }

    public void setTime24(String time24) {
        this.time24 = time24;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}