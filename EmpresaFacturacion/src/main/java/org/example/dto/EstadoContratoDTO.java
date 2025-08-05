package org.example.dto;
import org.example.enums.Estado;

import java.math.BigDecimal;

public class EstadoContratoDTO {
    private Long idContrato;
    private BigDecimal montoPagado;
    private Estado estadoActualizado;

    public EstadoContratoDTO() {}

    public EstadoContratoDTO(Long idContrato, BigDecimal montoPagado, Estado estadoActualizado) {
        this.idContrato = idContrato;
        this.montoPagado = montoPagado;
        this.estadoActualizado = estadoActualizado;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public Estado getEstadoActualizado() {
        return estadoActualizado;
    }

    public void setEstadoActualizado(Estado estadoActualizado) {
        this.estadoActualizado = estadoActualizado;
    }
}
