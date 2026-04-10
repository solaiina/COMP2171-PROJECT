package backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_settings")
public class SystemSetting {

    @Id
    private Long id = 1L;

    private Integer cancellationHoursNotice = 24;

    public SystemSetting() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCancellationHoursNotice() {
        return cancellationHoursNotice;
    }

    public void setCancellationHoursNotice(Integer cancellationHoursNotice) {
        this.cancellationHoursNotice = cancellationHoursNotice;
    }
}
