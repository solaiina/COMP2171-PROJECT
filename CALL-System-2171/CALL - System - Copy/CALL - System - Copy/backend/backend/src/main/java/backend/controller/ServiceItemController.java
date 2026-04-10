package backend.controller;

import backend.model.ServiceItem;
import backend.service.ServiceItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@CrossOrigin(origins = "*")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    public ServiceItemController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    @GetMapping
    public List<ServiceItem> getAllServices() {
        return serviceItemService.getAllServices();
    }

    @GetMapping("/provider/{email}")
    public List<ServiceItem> getServicesByProvider(@PathVariable String email) {
        return serviceItemService.getServicesByProvider(email);
    }

    @PostMapping
    public ResponseEntity<?> addService(@RequestBody ServiceItem serviceItem) {
        try {
            return ResponseEntity.ok(serviceItemService.addService(serviceItem));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody ServiceItem serviceItem) {
        try {
            return ResponseEntity.ok(serviceItemService.updateService(id, serviceItem));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        try {
            serviceItemService.deleteService(id);
            return ResponseEntity.ok("Service deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
