package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.ProvinciaService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Provincia;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.repositories.ProvinciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
public class ProvinciaServiceDB implements ProvinciaService {
  @Autowired
  ProvinciaRepository provinciaRepository;
  @Override
  public List<Provincia> getProvinciaList() {
    return provinciaRepository.findAll();
  }
}
