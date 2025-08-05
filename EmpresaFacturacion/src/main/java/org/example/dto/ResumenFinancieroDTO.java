package org.example.dto;
import java.math.BigDecimal;

public class ResumenFinancieroDTO {
    private Long idContrato;
    private String nombreCliente;
    private String tipoServicio;
    private BigDecimal montoEsperado;
    private BigDecimal montoPagado;

    public ResumenFinancieroDTO() {}

    public ResumenFinancieroDTO(Long idContrato, String nombreCliente, String tipoServicio, BigDecimal montoEsperado, BigDecimal montoPagado) {
        this.idContrato = idContrato;
        this.nombreCliente = nombreCliente;
        this.tipoServicio = tipoServicio;
        this.montoEsperado = montoEsperado;
        this.montoPagado = montoPagado;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public BigDecimal getMontoEsperado() {
        return montoEsperado;
    }

    public void setMontoEsperado(BigDecimal montoEsperado) {
        this.montoEsperado = montoEsperado;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }
}
