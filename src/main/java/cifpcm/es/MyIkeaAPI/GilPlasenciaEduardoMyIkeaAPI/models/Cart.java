package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
  @Id
  @Column(name = "user_id")
  private int id;
  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  private User owner;
  @ManyToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
  @JoinTable(
      name = "CART_PRODUCT",
      joinColumns = { @JoinColumn(name = "cart_id")},
      inverseJoinColumns = { @JoinColumn(name = "product_id")}
  )
  private List<Producto> productList;
  public Cart(User Owner){
    id = 0;
    owner = Owner;
    productList = new ArrayList<>();
  }
  public void addProduct(Producto product){
    productList.add(product);
  }
  public void removeProduct(Producto product){
    productList.remove(product);
  }
  public void removeAllProducts(){
    productList = new ArrayList<>();
  }
}

