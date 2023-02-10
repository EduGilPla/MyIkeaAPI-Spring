package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces;


import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
  List<Producto> getProductList();
  boolean addProduct(Producto newProduct);
  boolean deleteProduct(int id);
  boolean updateProduct(Producto toUpdate);
  Optional<Producto> findProduct(int id);
}
