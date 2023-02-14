package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.ProductoService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Cart;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Producto;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.User;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.UserServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class CartController {
  @Autowired
  UserServiceDB userService;
  @Autowired
  ProductoService productoService;
  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping("/addToCart/{id}")
  public ResponseEntity<Producto> addToCart(@PathVariable String id, Authentication authentication){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    User user = userQuery.get();
    Optional<Producto> productQuery = productoService.findProduct(Integer.parseInt(id));
    if(productQuery.isEmpty()){
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Producto product = productQuery.get();
    Cart userCart = userQuery.get().getCart();
    userCart.addProduct(product);
    userService.saveUserCart(user);
    return new ResponseEntity<>(product,HttpStatus.ACCEPTED);
  }
  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping("/removeFromCart/{id}")
  public ResponseEntity<Producto> removeFromCart(@PathVariable String id, Authentication authentication){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    User user = userQuery.get();
    Optional<Producto> productQuery = productoService.findProduct(Integer.parseInt(id));
    if(productQuery.isEmpty()){
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    Producto product = productQuery.get();
    Cart userCart = userQuery.get().getCart();
    userCart.removeProduct(product);
    userService.saveUserCart(user);
    return new ResponseEntity<>(product,HttpStatus.ACCEPTED);
  }
  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping("/customer/cart")
  public ResponseEntity<List<Producto>> showCart(Authentication authentication){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    User user = userQuery.get();
    Cart cart = user.getCart();
    List<Producto> cartList = cart.getProductList();

    return new ResponseEntity<>(cartList,HttpStatus.OK);
  }
}
