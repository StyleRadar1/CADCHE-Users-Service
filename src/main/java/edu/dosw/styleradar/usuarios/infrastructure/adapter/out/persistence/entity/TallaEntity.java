package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity;

import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import jakarta.persistence.*;

@Entity
@Table(
        name = "usuario_tallas",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_usuario_tipo_talla",
                columnNames = {"usuario_id", "tipo_prenda", "talla"}
        )
)
public class TallaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_prenda", nullable = false, length = 20)
    private TipoPrenda tipoPrenda;

    @Column(nullable = false, length = 10)
    private String talla;

    public TallaEntity() {
    }

    public TallaEntity(UsuarioEntity usuario, TipoPrenda tipoPrenda, String talla) {
        this.usuario = usuario;
        this.tipoPrenda = tipoPrenda;
        this.talla = talla;
    }

    public Long getId() {
        return id;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public TipoPrenda getTipoPrenda() {
        return tipoPrenda;
    }

    public String getTalla() {
        return talla;
    }
}
