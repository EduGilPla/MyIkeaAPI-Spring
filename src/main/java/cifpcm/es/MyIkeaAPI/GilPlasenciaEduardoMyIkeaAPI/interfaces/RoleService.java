package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
  List<Role> getRoleList();

  Optional<Role> findRoleByName(String name);
  void saveRoleList(List<Role> roles);
}
