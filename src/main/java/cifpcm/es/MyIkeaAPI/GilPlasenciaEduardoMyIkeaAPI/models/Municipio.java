package cifpcm.es.MyIkeaAPI.GilPlasenciaEduardoMyIkeaAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "municipios")
@JsonIgnoreProperties(value = "productos")
public class Municipio {
  @Id
  @GeneratedValue
  private int id_municipio;
  @ManyToOne
  @JoinColumn(name = "id_provincia")
  private Provincia provincia;
  @OneToMany(mappedBy = "municipio",
      cascade = CascadeType.MERGE,
      orphanRemoval = true)

  private List<Producto> productos;
  private int cod_municipio;
  private int DC;
  private String nombre;
}
