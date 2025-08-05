package org.example.dto;

import org.example.enums.TipoServicio;

import java.math.BigDecimal;
public class ResumenCanceladosDTO {
    private TipoServicio tipoServicio;
    private Long cantidadContratos;
    private BigDecimal montoTotalCobrado;

    public ResumenCanceladosDTO() {}

    public ResumenCanceladosDTO(TipoServicio tipoServicio, Long cantidadContratos, BigDecimal montoTotalCobrado) {
        this.tipoServicio = tipoServicio;
        this.cantidadContratos = cantidadContratos;
        this.montoTotalCobrado = montoTotalCobrado;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public Long getCantidadContratos() {
        return cantidadContratos;
    }

    public void setCantidadContratos(Long cantidadContratos) {
        this.cantidadContratos = cantidadContratos;
    }

    public BigDecimal getMontoTotalCobrado() {
        return montoTotalCobrado;
    }

    public void setMontoTotalCobrado(BigDecimal montoTotalCobrado) {
        this.montoTotalCobrado = montoTotalCobrado;
    }
}
