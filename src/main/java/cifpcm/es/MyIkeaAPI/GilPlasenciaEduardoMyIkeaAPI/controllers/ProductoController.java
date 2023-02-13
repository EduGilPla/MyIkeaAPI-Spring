package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.ProductoService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.ProvinciaService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Producto;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.UserServiceDB;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


@RestController
public class ProductoController {
  @Autowired
  ProductoService productoService;
  @Autowired
  UserServiceDB userService;
  @Autowired
  ProvinciaService provinciaService;
  public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images/";
  private final String ErrorAttributeName = "error";
  @GetMapping("/products")
  public ResponseEntity<List<Producto>> List() {
    return new ResponseEntity<>(productoService.getProductList(), HttpStatus.OK);
  }
  //@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
  @PostMapping("/products/create")
  public ResponseEntity<Producto> Create(@Valid @ModelAttribute("newProduct") Producto newProduct, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return new ResponseEntity<>(newProduct,HttpStatus.BAD_REQUEST);
    }
    if (!productoService.addProduct(newProduct)) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(newProduct,HttpStatus.CREATED);
  }

  @GetMapping("/products/details/{id}")
  public ResponseEntity<Producto> Details(@PathVariable String id) {
    Optional<Producto> foundProduct = productoService.findProduct(Integer.parseInt(id));
    if (foundProduct.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(foundProduct.get(),HttpStatus.OK);
  }
  //@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
  @DeleteMapping("/products/delete/{id}")
  public ResponseEntity<Producto> Delete(@PathVariable String id) {
    Optional<Producto> productToDelete = productoService.findProduct(Integer.parseInt(id));
    if (productToDelete.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(productToDelete.get(),HttpStatus.OK);
  }
  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
  @GetMapping("/products/update/{id}")
  public String Update(@PathVariable String id, Model ViewData) {
    Optional<Producto> productoToUpdate = productoService.findProduct(Integer.parseInt(id));
    if (productoToUpdate.isEmpty()) {
      String PRODUCT_NOT_FOUND_ERROR = "No se ha podido actualizar el producto. ( El producto con id " + id + " no existe)";
      ViewData.addAttribute(ErrorAttributeName, PRODUCT_NOT_FOUND_ERROR);
      ViewData.addAttribute("productList", productoService.getProductList());
      return "/products/list";
    }
    ViewData.addAttribute("product", productoToUpdate.get());
    ViewData.addAttribute("provinceList", provinciaService.getProvinciaList());
    return "/products/update";
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
  @PostMapping("/products/update/{id}")
  public String Update(@Valid @ModelAttribute("product") Producto modifiedProduct, BindingResult bindingResult, Model ViewData) {
    if (bindingResult.hasErrors()) {
      ViewData.addAttribute("product", modifiedProduct);
      return "/products/update";
    }
    if (!productoService.updateProduct(modifiedProduct)) {
      String DATABASE_ERROR = "No se ha podido modificar el producto. (Error de SQL)";
      ViewData.addAttribute("productList", productoService.getProductList());
      ViewData.addAttribute(ErrorAttributeName, DATABASE_ERROR);
      return "/products/list";
    }
    return "redirect:/products";
  }
}
