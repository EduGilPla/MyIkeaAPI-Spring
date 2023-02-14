package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Cart;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Order;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Producto;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.User;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.UserServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CustomerController {
  @Autowired
  UserServiceDB userService;
  private final String ErrorAttributeName = "error";
  @GetMapping("/order")
  public String placeOrder(Authentication authentication, Model ViewData){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      String USER_NOT_FOUND_ERROR = "No se ha podido llevar a cabo el pedido. (Usuario no identificado)";
      ViewData.addAttribute(ErrorAttributeName,USER_NOT_FOUND_ERROR);
      return "/products/list";
    }
    User user = userQuery.get();
    Cart cart = user.getCart();
    List<Producto> productList = cart.getProductList();
    if(productList.isEmpty()){
      String EMPTY_CART_ERROR = "No se ha podido llevar a cabo el pedido. (El carro está vacío)";
      ViewData.addAttribute(ErrorAttributeName,EMPTY_CART_ERROR);
      ViewData.addAttribute("totalPrice",0);
      return "/customer/cart";
    }
    int totalPrice = 0;
    for(Producto product : productList){
      totalPrice+= product.getProduct_price();
    }
    if(totalPrice == 0){
      String INVALID_PRICE_ERROR = "No se ha podido llevar a cabo el pedido. (El precio es 0)";
      ViewData.addAttribute(ErrorAttributeName,INVALID_PRICE_ERROR);
      ViewData.addAttribute("totalPrice",0);
      return "/customer/cart";
    }
    Order newOrder = new Order(productList,user,totalPrice);
    user.addOrder(newOrder);
    cart.removeAllProducts();
    if(!userService.saveUserCart(user)){
      String PERSISTENCE_FAILED_ERROR = "El pedido se ha llevado a cabo, pero no se ha guardado en la base de datos. Los cambios se perderán al cerrar sesión. (Error de SQL)";
      ViewData.addAttribute(ErrorAttributeName,PERSISTENCE_FAILED_ERROR);
      ViewData.addAttribute("totalPrice",0);
      return "/customer/cart";
    }
    return "redirect:/customer/orders";
  }
  @GetMapping("/customer/orders")
  public String showOrders(Authentication authentication, Model ViewData){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      String USER_NOT_FOUND_ERROR = "No se han podido mostrar los pedidos. (Usuario no identificado)";
      ViewData.addAttribute(ErrorAttributeName,USER_NOT_FOUND_ERROR);
      return "/products/list";
    }
    User user = userQuery.get();
    ViewData.addAttribute("orders",user.getOrders());
    return "/customer/orders";
  }
  @GetMapping("/customer/orders/details/{id}")
  public String orderDetails(@PathVariable String id, Authentication authentication, Model ViewData){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      String USER_NOT_FOUND_ERROR = "No se han podido mostrar los detalles del pedido con id " + id + ". (Usuario no identificado)";
      ViewData.addAttribute(ErrorAttributeName,USER_NOT_FOUND_ERROR);
      return "/products/list";
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
      String ORDER_NOT_FOUND_ERROR = "No se han podido mostrar los detalles del pedido con id " + id + ". (Pedido no encontrado)";
      ViewData.addAttribute("orders",user.getOrders());
      ViewData.addAttribute(ErrorAttributeName,ORDER_NOT_FOUND_ERROR);
      return "/customer/orders";
    }
    List<Producto> orderProducts = new ArrayList<>();
    for(Producto product : orderToDetail.getProducts()){
      if(orderProducts.contains(product))
        product.plusOne();
      else
        orderProducts.add(product);
    }
    ViewData.addAttribute("orderProducts",orderProducts);
    ViewData.addAttribute("order",orderToDetail);
    return "/customer/orderDetails";
  }
}
