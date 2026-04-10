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
        return serviceItemRepository.findAll();
    }

    public ServiceItem addService(ServiceItem serviceItem) {
        return serviceItemRepository.save(serviceItem);
    }

    public ServiceItem updateService(Long id, ServiceItem updatedService) {
        ServiceItem existingService = serviceItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found."));

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
}