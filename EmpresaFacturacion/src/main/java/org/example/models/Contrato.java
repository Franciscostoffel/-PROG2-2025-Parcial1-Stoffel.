package org.example.models;
import jakarta.persistence.*;
import org.example.enums.Estado;
import org.example.enums.TipoServicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "contrato_servicio")
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato")
    private Long id;

    @Column(name = "nombre_cliente", nullable = false)
    private String nombreCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio", nullable = false)
    private TipoServicio tipoServicio;

    @Column(name = "tarifa_mensual", nullable = false)
    private BigDecimal tarifaMensual;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.ACTIVO;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos;

    public Contrato() {}
    public Contrato(Long id, String nombreCliente, TipoServicio tipoServicio,
                    BigDecimal tarifaMensual, LocalDate fechaInicio,
                    LocalDate fechaFin, Estado estado) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.tipoServicio = tipoServicio;
        this.tarifaMensual = tarifaMensual;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }
public Long getId() {
        return id;
    }
public void setId(Long id) {
        this.id = id;
    }
public String getNombreCliente() {
        return nombreCliente;
    }
public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
public TipoServicio getTipoServicio() {
        return tipoServicio;
    }
public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }
public BigDecimal getTarifaMensual() {
        return tarifaMensual;
    }
public void setTarifaMensual(BigDecimal tarifaMensual) {
        this.tarifaMensual = tarifaMensual;
    }
public LocalDate getFechaInicio() {
        return fechaInicio;
    }
public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
public LocalDate getFechaFin() {
        return fechaFin;
    }
public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
public Estado getEstado() {
        return estado;
    }
public void setEstado(Estado estado) {
        this.estado = estado;
    }
public List<Pago> getPagos() {
        return pagos;
    }
public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }
public void addPago(Pago pago) {
        pagos.add(pago);
        pago.setContrato(this);
    }

    // Getters y setters...
}
