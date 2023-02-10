package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.ProductoService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CustomerController {
  @Autowired
  UserServiceDB userService;
  @Autowired
  ProductoService productoService;
  private final String ErrorAttributeName = "error";
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/addToCart/{id}")
  public String addToCart(@PathVariable String id, Authentication authentication, Model ViewData, RedirectAttributes redirectAttributes){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      String USER_NOT_FOUND_ERROR = "No se ha podido añadir el objeto al carrito. (Usuario no identificado)";
      ViewData.addAttribute(ErrorAttributeName,USER_NOT_FOUND_ERROR);
      return "/products/list";
    }
    User user = userQuery.get();
    Optional<Producto> productQuery = productoService.findProduct(Integer.parseInt(id));
    if(productQuery.isEmpty()){
      String PRODUCT_NOT_FOUND_ERROR = "No se ha podido añadir el objeto al carrito. (El producto con id: " + id + " no existe)";
      ViewData.addAttribute(ErrorAttributeName,PRODUCT_NOT_FOUND_ERROR);
      return "/products/list";
    }
    Producto product = productQuery.get();
    Cart userCart = userQuery.get().getCart();
    userCart.addProduct(product);
    userService.saveUserCart(user);

    redirectAttributes.addAttribute("product",product.getProduct_name() + " añadido al carrito");
    return "redirect:/products";
  }
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/removeFromCart/{id}")
  public String removeFromCart(@PathVariable String id, Authentication authentication, Model ViewData){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      String USER_NOT_FOUND_ERROR = "No se ha podido eliminar el objeto del carrito. (Usuario no identificado)";
      ViewData.addAttribute(ErrorAttributeName,USER_NOT_FOUND_ERROR);
      return "/products/list";
    }
    User user = userQuery.get();
    Optional<Producto> productQuery = productoService.findProduct(Integer.parseInt(id));
    if(productQuery.isEmpty()){
      String PRODUCT_NOT_FOUND_ERROR = "No se ha podido eliminar el objeto del carrito. (El producto con id: " + id + " no existe)";
      ViewData.addAttribute(ErrorAttributeName,PRODUCT_NOT_FOUND_ERROR);
      return "/products/list";
    }
    Producto product = productQuery.get();
    Cart userCart = userQuery.get().getCart();
    userCart.removeProduct(product);
    userService.saveUserCart(user);
    return "redirect:/customer/cart";
  }
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/customer/cart")
  public String showCart(Authentication authentication, Model ViewData){
    Optional<User> userQuery = userService.findUserByEmail(authentication.getName());
    if (userQuery.isEmpty()){
      String USER_NOT_FOUND_ERROR = "No se ha podido mostrar el carrito. (Usuario no identificado)";
      ViewData.addAttribute(ErrorAttributeName,USER_NOT_FOUND_ERROR);
      return "/products/list";
    }
    User user = userQuery.get();
    Cart cart = user.getCart();
    List<Producto> cartList = cart.getProductList();
    List<Producto> noRepetitionProductList = new ArrayList<>();
    int cartTotal = 0;
    for(Producto product : cartList){
      cartTotal += product.getProduct_price();
      if(noRepetitionProductList.contains(product))
        product.plusOne();
      else
        noRepetitionProductList.add(product);
    }
    ViewData.addAttribute("totalPrice", cartTotal);
    ViewData.addAttribute("cart",noRepetitionProductList);
    return "/customer/cart";
  }
  @PreAuthorize("hasRole('ROLE_USER')")
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
  @PreAuthorize("hasRole('ROLE_USER')")
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
  @PreAuthorize("hasRole('ROLE_USER')")
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
