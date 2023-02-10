package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.CartService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Cart;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceDB implements CartService {
  @Autowired
  CartRepository cartRepository;

  @Override
  public Optional<Cart> findCart(int id){ return cartRepository.findById(id); }
}
