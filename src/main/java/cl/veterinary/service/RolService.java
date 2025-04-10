package cl.veterinary.service;

import cl.veterinary.model.Rol;
import java.util.Optional;

public interface RolService {

    Optional<Rol> finRolById(Long id);
    Rol saveRol(Rol rol);
    Rol updateRol(Rol rol);
    void deleteRol(Long id);
}
