package backend.repository;

import backend.model.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findAllByOrderByNameAsc();
    List<ServiceItem> findByProviderEmailOrderByNameAsc(String providerEmail);
}
