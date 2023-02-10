package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.RoleService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Role;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
public class RoleServiceDB implements RoleService {
  @Autowired
  RoleRepository roleRepository;
  @Override
  public List<Role> getRoleList() {
    return roleRepository.findAll();
  }
  public void saveRoleList(List<Role> roles){
    roleRepository.saveAll(roles);
  }
  @Override
  public Optional<Role> findRoleByName(String name) {
    return roleRepository.findByName(name);
  }
}
