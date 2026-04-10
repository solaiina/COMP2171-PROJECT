package backend.service;

import backend.model.ServiceItem;
import backend.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;

    public ServiceItemService(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    public List<ServiceItem> getAllServices() {
        return serviceItemRepository.findAllByOrderByNameAsc();
    }

    public List<ServiceItem> getServicesByProvider(String providerEmail) {
        return serviceItemRepository.findByProviderEmailOrderByNameAsc(providerEmail);
    }

    public ServiceItem addService(ServiceItem serviceItem) {
        validateService(serviceItem);

        return serviceItemRepository.save(serviceItem);
    }

    public ServiceItem updateService(Long id, ServiceItem updatedService) {
        validateService(updatedService);

        ServiceItem existingService = serviceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found."));

        existingService.setProviderEmail(updatedService.getProviderEmail());
        existingService.setProviderName(updatedService.getProviderName());
        existingService.setName(updatedService.getName());
        existingService.setDuration(updatedService.getDuration());
        existingService.setPrice(updatedService.getPrice());

        return serviceItemRepository.save(existingService);
    }

    public void deleteService(Long id) {
        if (!serviceItemRepository.existsById(id)) {
            throw new RuntimeException("Service not found.");
        }

        serviceItemRepository.deleteById(id);
    }

    private void validateService(ServiceItem serviceItem) {
        if (serviceItem.getProviderEmail() == null || serviceItem.getProviderEmail().isBlank()) {
            throw new RuntimeException("Provider email is required.");
        }

        if (serviceItem.getName() == null || serviceItem.getName().isBlank()) {
            throw new RuntimeException("Service name is required.");
        }

        if (serviceItem.getDuration() == null || serviceItem.getDuration() <= 0) {
            throw new RuntimeException("Service duration must be greater than 0.");
        }

        if (serviceItem.getPrice() == null || serviceItem.getPrice() < 0) {
            throw new RuntimeException("Service price must be 0 or greater.");
        }
    }
}
