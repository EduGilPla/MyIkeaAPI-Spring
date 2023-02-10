package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces;


import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Cart;

import java.util.Optional;

public interface CartService {
  Optional<Cart> findCart(int id);
}
