package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces;


import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
  List<Producto> getProductList();
  boolean saveProduct(Producto newProduct);
  boolean deleteProduct(int id);
  Optional<Producto> findProduct(int id);
}
