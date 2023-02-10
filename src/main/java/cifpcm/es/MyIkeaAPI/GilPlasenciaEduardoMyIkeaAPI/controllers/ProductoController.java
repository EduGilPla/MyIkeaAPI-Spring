package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.ProductoService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.ProvinciaService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Producto;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.services.UserServiceDB;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;


@Controller
public class ProductoController {
  @Autowired
  ProductoService productoService;
  @Autowired
  UserServiceDB userService;
  @Autowired
  ProvinciaService provinciaService;
  public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images/";
  private final String ErrorAttributeName = "error";

  @GetMapping("/")
  public String Start() {
    return "/common/welcome";
  }

  @GetMapping("/products")
  public String List(Model ViewData,@RequestParam("product") Optional<String> addedProduct) {
    if(addedProduct.isPresent())
      ViewData.addAttribute("addedProduct",addedProduct.get());
    ViewData.addAttribute("productList", productoService.getProductList());
    return "/products/list";
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
  @GetMapping("/products/create")
  public String Create(Model ViewData) {
    Producto newProduct = new Producto();
    ViewData.addAttribute("newProduct", newProduct);
    ViewData.addAttribute("provinceList", provinciaService.getProvinciaList());
    return "/products/create";
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
  @PostMapping("/products/create")
  public String Create(@Valid @ModelAttribute("newProduct") Producto newProduct, BindingResult bindingResult, Model ViewData, @RequestParam("img") MultipartFile img) {
    StringBuilder fileNames = new StringBuilder();
    newProduct.setProduct_picture(img.getOriginalFilename());
    if (bindingResult.hasErrors()) {
      ViewData.addAttribute("provinceList", provinciaService.getProvinciaList());
      return "/products/create";
    }
    try {
      Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, img.getOriginalFilename());
      fileNames.append(img.getOriginalFilename());
      Files.write(fileNameAndPath, img.getBytes());
    } catch (IOException exception) {
      ViewData.addAttribute("productList", productoService.getProductList());
      ViewData.addAttribute(ErrorAttributeName, exception.getMessage());
      return "/products/list";
    }
    if (productoService.addProduct(newProduct)) {
      return "redirect:/products";
    }
    String SERVER_ERROR = "No se ha podido crear el producto, fallo de servidor";
    ViewData.addAttribute("productList", productoService.getProductList());
    ViewData.addAttribute(ErrorAttributeName,SERVER_ERROR);
    return "/products/list";
  }

  @GetMapping("/products/details/{id}")
  public String Details(@PathVariable String id, Model ViewData) {

    Optional<Producto> foundProduct = productoService.findProduct(Integer.parseInt(id));

    if (foundProduct.isEmpty()) {
      String PRODUCT_NOT_FOUND_ERROR = "No se han podido mostrar los detalles del producto. ( El producto con id " + id + " no existe)";
      ViewData.addAttribute(ErrorAttributeName, PRODUCT_NOT_FOUND_ERROR);
      ViewData.addAttribute("productList", productoService.getProductList());
      return "/products/list";
    }
    ViewData.addAttribute("product", foundProduct.get());
    return "/products/details";
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
  @GetMapping("/products/delete/{id}")
  public String Delete(@PathVariable String id, Model ViewData) {

    Optional<Producto> productToDelete = productoService.findProduct(Integer.parseInt(id));

    if (productToDelete.isEmpty()) {
      String PRODUCT_NOT_FOUND_ERROR = "No se ha podido eliminar el producto. ( El producto con id " + id + " no existe)";
      ViewData.addAttribute(ErrorAttributeName, PRODUCT_NOT_FOUND_ERROR);
      ViewData.addAttribute("productList", productoService.getProductList());
      return "/products/list";
    }
    ViewData.addAttribute("product", productToDelete.get());
    return "/products/delete";
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
  @PostMapping("/products/delete/{id}")
  public String DeletePost(@PathVariable String id, Model ViewData) {
    if (!productoService.deleteProduct(Integer.parseInt(id))) {
      String DATABASE_ERROR = "No se ha podido eliminar el producto. (Error de SQL)";
      ViewData.addAttribute(ErrorAttributeName, DATABASE_ERROR);
      ViewData.addAttribute("productList", productoService.getProductList());
      return "/products/list";
    }
    return "redirect:/products";
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
