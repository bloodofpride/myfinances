package com.mponte.minhasfinancas.model.entities;

import com.mponte.minhasfinancas.model.enums.StatusLancamento;
import com.mponte.minhasfinancas.model.enums.TipoLancamento;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "lancamento", schema = "financas")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Lancamento {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private Integer mes;
    private Integer ano;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private BigDecimal valor;

    @Column(name = "data_cadastro")
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate dataCadastro;
    @Enumerated(value = EnumType.STRING)
    private TipoLancamento tipo;
    @Enumerated(value = EnumType.STRING)
    private StatusLancamento status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Lancamento that = (Lancamento) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
