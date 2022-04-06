package com.mponte.minhasfinancas.model.dtos;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class LancamentoDTO implements Serializable {
    private static final long serialVersionUID = -3208666400086174924L;
    private Long id;
    private String descricao;
    private Integer mes;
    private Integer ano;
    private Long usuario;
    private BigDecimal valor;
    private String tipo;
    private String status;
}
