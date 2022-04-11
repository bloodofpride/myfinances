package com.mponte.minhasfinancas.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "usuario", schema = "financas")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    @JsonIgnore
    private String senha;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Usuario usuario = (Usuario) o;
        return id != null && Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
