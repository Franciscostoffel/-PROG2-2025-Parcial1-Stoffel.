package org.example.dto;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FiltroContratoDTO {
    private String nombreCliente;
    private String tipoServicio;
    private LocalDate fechaInicioDesde;
    private LocalDate fechaInicioHasta;
    private BigDecimal tarifaDesde;
    private BigDecimal tarifaHasta;

    public FiltroContratoDTO() {}

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

    public LocalDate getFechaInicioDesde() {
        return fechaInicioDesde;
    }

    public void setFechaInicioDesde(LocalDate fechaInicioDesde) {
        this.fechaInicioDesde = fechaInicioDesde;
    }

    public LocalDate getFechaInicioHasta() {
        return fechaInicioHasta;
    }

    public void setFechaInicioHasta(LocalDate fechaInicioHasta) {
        this.fechaInicioHasta = fechaInicioHasta;
    }

    public BigDecimal getTarifaDesde() {
        return tarifaDesde;
    }

    public void setTarifaDesde(BigDecimal tarifaDesde) {
        this.tarifaDesde = tarifaDesde;
    }

    public BigDecimal getTarifaHasta() {
        return tarifaHasta;
    }

    public void setTarifaHasta(BigDecimal tarifaHasta) {
        this.tarifaHasta = tarifaHasta;
    }
}
