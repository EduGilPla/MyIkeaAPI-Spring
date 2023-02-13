package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "provincias")
@JsonIgnoreProperties(value = "municipios")
public class Provincia {
  @Id
  @GeneratedValue
  private int id_provincia;
  @NotBlank
  @Size(min = 1, max = 20)
  private String nombre;
  @OneToMany(mappedBy = "provincia",
      cascade = CascadeType.MERGE,
      orphanRemoval = true)
  private List<Municipio> municipios;
}
