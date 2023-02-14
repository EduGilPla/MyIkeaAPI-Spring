package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.ProductoService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Cart;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Producto;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.User;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.UserServiceDB;
import org.springframework.beans.factory.annotation.Autowired;
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
  private final String ErrorAttributeName = "error";
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
}
