package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue
  private int id;
  @ManyToOne
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User buyer;

  @ManyToMany(cascade = {CascadeType.DETACH},fetch = FetchType.EAGER)
  @JoinTable(
      name = "ORDER_PRODUCT",
      joinColumns = { @JoinColumn(name = "order_id")},
      inverseJoinColumns = { @JoinColumn(name = "product_id")}
  )
  private List<Producto> products;
  private Date orderDate;

  private int totalPrice;

  public Order(List<Producto> Products, User Buyer, int Price){
    buyer = Buyer;
    products = Products;
    orderDate = new Date();
    totalPrice = Price;
  }
}
