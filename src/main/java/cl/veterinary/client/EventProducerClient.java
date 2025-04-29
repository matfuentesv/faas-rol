package cl.veterinary.client;

import cl.veterinary.model.RolEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "eventProducerClient", url = "https://event-producer-function.azurewebsites.net")
public interface EventProducerClient {

    @PostMapping(value = "/api/RoleCrudFunction", consumes = "application/json")
    void eventPost(
            @RequestParam("code") String code,
            @RequestParam("operation") String operation,
            @RequestBody RolEvent dto
    );

    @GetMapping("/api/RoleCrudFunction")
    String eventGet(
            @RequestParam("code") String code,
            @RequestParam("operation") String operation,
            @RequestParam("id") Long id
    );
}
