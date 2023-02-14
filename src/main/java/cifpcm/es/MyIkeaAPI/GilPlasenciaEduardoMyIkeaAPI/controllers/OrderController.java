package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Cart;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Order;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderController {
  @Autowired
  UserServiceDB userService;
  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping("/order")
  public ResponseEntity<Order> placeOrder(Authentication authentication){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    User user = userQuery.get();
    Cart cart = user.getCart();
    List<Producto> productList = cart.getProductList();
    if(productList.isEmpty()){
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    int totalPrice = 0;
    for(Producto product : productList){
      totalPrice+= product.getProduct_price();
    }
    Order newOrder = new Order(productList,user,totalPrice);
    user.addOrder(newOrder);
    cart.removeAllProducts();
    if(!userService.saveUserCart(user)){
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(newOrder,HttpStatus.OK);
  }
  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping("/customer/orders")
  public ResponseEntity<List<Order>> showOrders(Authentication authentication){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    User user = userQuery.get();
    return new ResponseEntity<>(user.getOrders(),HttpStatus.OK);
  }
  @PreAuthorize("hasAuthority('ROLE_USER')")
  @GetMapping("/customer/orders/details/{id}")
  public ResponseEntity<Order> orderDetails(@PathVariable String id, Authentication authentication){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    User user = userQuery.get();
    Order orderToDetail = null;
    for(Order order : user.getOrders()){
      if(order.getId() == Integer.parseInt(id)){
        orderToDetail = order;
        break;
      }
    }
    if(orderToDetail == null){
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    return new ResponseEntity<>(orderToDetail,HttpStatus.OK);
  }
}
