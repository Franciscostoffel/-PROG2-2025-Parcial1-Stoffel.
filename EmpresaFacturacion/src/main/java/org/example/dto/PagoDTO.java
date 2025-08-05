package org.example.dto;
import java.math.BigDecimal;

public class PagoDTO {
    private Long idContrato;
    private BigDecimal monto;

    public PagoDTO() {}

    public PagoDTO(Long idContrato, BigDecimal monto) {
        this.idContrato = idContrato;
        this.monto = monto;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
