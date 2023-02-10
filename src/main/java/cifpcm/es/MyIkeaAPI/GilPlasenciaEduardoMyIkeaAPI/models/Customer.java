package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer")
public class Customer {
  @Id
  @GeneratedValue
  private int customer_id;
  @NotBlank
  @Size(min = 1, max = 45)
  private String first_name;
  @NotBlank
  @Size(min = 1, max = 45)
  private String last_name;
  @NotBlank
  @Size(min = 9,max = 9)
  private String telefono;
  @NotBlank
  @Size(min = 1,max = 50)
  private String email;
  private Date fecha_de_nacimiento;
}
