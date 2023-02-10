package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "productoffer")
public class Producto {
  @Id
  @GeneratedValue
  private int product_id;
  @NotBlank(message = "El nombre es obligatorio")
  @NotNull
  @Size(min = 1, max = 20,message = "El nombre debe estar entre 1 y 20 caracteres")
  private String product_name;
  @NotNull(message = "El precio no puede ser nulo")
  @Min(value = 1,message = "El producto debe valer al menos 1 euro")
  private int product_price;
  @NotBlank(message = "Debes seleccionar un archivo")
  @NotNull(message = "Debes seleccionar una foto")
  @Size(max = 45,message = "El nombre de archivo es demasiado largo (mas de 45 caracteres)")
  private String product_picture;
  @ManyToOne
  @JoinColumn(name = "id_municipio")
  @NotNull(message = "Debes seleccionar un municipio")
  private Municipio municipio;
  @NotNull(message = "El valor de productos no puede ser nulo")
  @Min(value = 1,message = "Debe haber al menos 1 unidad en stock")
  private int product_stock;
  @ManyToMany(mappedBy = "productList",
      fetch = FetchType.EAGER)
  private List<Cart> carts;
  @ManyToMany(mappedBy = "products",
      fetch = FetchType.EAGER)
  private List<Order> orders;
  @Transient
  private int quantity = 1;

  public void plusOne(){
    quantity++;
  }
}
