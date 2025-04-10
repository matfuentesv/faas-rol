package cl.veterinary;

import java.util.Optional;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import cl.veterinary.model.Rol;
import cl.veterinary.service.RolService;


public class RolFunction {

    private static final ApplicationContext context =
            new SpringApplicationBuilder(SpringBootAzureApp.class).run();

    private final RolService rolService =
            context.getBean(RolService.class); // usa la interfaz



    @FunctionName("saveRoles")
    public HttpResponseMessage saveCustomer(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "saveRoles")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud saveRoles...");

        try {

            String requestBody = request.getBody().orElse("");
            ObjectMapper mapper = new ObjectMapper();
            Rol roles = mapper.readValue(requestBody, Rol.class);

            Rol saved = rolService.saveRol(roles);

            return request
                .createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json") 
                .body(saved)
                .build();

        } catch (Exception e) {
            context.getLogger().severe("Error al guardar roles: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar roles")
                    .build();
        }
    }

    @FunctionName("updateRoles")
    public HttpResponseMessage updateCustomer(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "updateRoles/{id}") // id por ruta
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud updateRoles con ID: " + id);

        try {
            Long customerId = Long.parseLong(id);

            // Parsear el JSON recibido
            String requestBody = request.getBody().orElse("");
            ObjectMapper mapper = new ObjectMapper();
            Rol updatedData = mapper.readValue(requestBody, Rol.class);

            // Buscar si el cliente existe
            Optional<Rol> existingOpt = rolService.finRolById(customerId);
            if (existingOpt.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("roles con ID " + id + " no encontrado.")
                        .build();
            }

            Rol existing = existingOpt.get();

            // Actualizar campos
            existing.setId(customerId);
            existing.setNombre(updatedData.getNombre());
            existing.setDescripcion(updatedData.getDescripcion());

            // Guardar cambios
            Rol updated = rolService.updateRol(existing);

            return request
                .createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json") 
                .body(updated)
                .build();

        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("ID inválido: debe ser numérico.")
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error al actualizar roles: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar roles.")
                    .build();
        }
    }

    @FunctionName("deleteRoles")
    public HttpResponseMessage deleteCustomer(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "deleteRoles/{id}") // ID por ruta
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud deleteRoles con ID: " + id);

        try {
            Long rolId = Long.parseLong(id);

            // Buscar si existe
            Optional<Rol> existing = rolService.finRolById(rolId);
            if (existing.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body(new Rol())
                        .build();
            }

            // Eliminar
            rolService.deleteRol(rolId);

            return request
                .createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json") 
                .body("Rol eliminado")
                .build();

        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("ID inválido: debe ser numérico.")
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error al eliminar rol: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar rol.")
                    .build();
        }
    }

}
