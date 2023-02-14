package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.controllers;

import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.interfaces.ProductoService;
import cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models.Producto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
public class ProductoController {
  @Autowired
  ProductoService productoService;
  @GetMapping("/products")
  public ResponseEntity<List<Producto>> List() {
    return new ResponseEntity<>(productoService.getProductList(), HttpStatus.OK);
  }
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
  @PostMapping("/products/create")
  public ResponseEntity<Producto> Create(@Valid @RequestBody Producto newProduct, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return new ResponseEntity<>(newProduct,HttpStatus.BAD_REQUEST);
    }
    if (!productoService.saveProduct(newProduct)) {
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
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
  @DeleteMapping("/products/delete/{id}")
  public ResponseEntity<Producto> Delete(@PathVariable String id) {
    Optional<Producto> productToDelete = productoService.findProduct(Integer.parseInt(id));
    if (productToDelete.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if(!productoService.deleteProduct(Integer.parseInt(id))){
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(productToDelete.get(),HttpStatus.OK);
  }
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
  @PutMapping("/products/update/{id}")
  public ResponseEntity<Producto> Update(@Valid @RequestBody Producto modifiedProduct, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return new ResponseEntity<>(modifiedProduct,HttpStatus.BAD_REQUEST);
    }
    if (!productoService.saveProduct(modifiedProduct)) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(modifiedProduct,HttpStatus.CREATED);
  }
}
