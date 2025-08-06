package org.example;

import org.example.dto.PagoDTO;
import org.example.dto.FiltroContratoDTO;
import org.example.enums.Estado;
import org.example.enums.TipoServicio;
import org.example.models.ContratoServicio;
import org.example.service.PagoService;
import org.example.utils.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        EntityManager em = HibernateUtil.getSession().unwrap(EntityManager.class);
        em.getTransaction().begin();
        ContratoServicio c1 = new ContratoServicio(null, "Juan Perez", TipoServicio.AGUA,
                new BigDecimal("1000"), LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 1), Estado.ACTIVO);
        ContratoServicio c2 = new ContratoServicio(null, "Ana Lopez", TipoServicio.ELECTRICIDAD,
                new BigDecimal("1500"), LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 7, 1), Estado.ACTIVO);
        em.persist(c1);
        em.persist(c2);
        em.getTransaction().commit();
        PagoService pagoService = PagoService.getInstancia();
        pagoService.registrarPago(new PagoDTO(c1.getId(), new BigDecimal("2000")));
        pagoService.registrarPago(new PagoDTO(c2.getId(), new BigDecimal("1500")));
        // Buscar contratos por nombre
        FiltroContratoDTO filtro = new FiltroContratoDTO();
        filtro.setNombreCliente("Juan");
        System.out.println("🔎 Contratos encontrados:");
        pagoService.buscarContratos(filtro).forEach(c -> {
            System.out.println(" - ID: " + c.getIdContrato() +
                    ", Cliente: " + c.getNombreCliente() +
                    ", Servicio: " + c.getTipoServicio() +
                    ", Estado: " + c.getEstado() +
                    ", Tarifa: $" + c.getTarifaMensual());
        });
        // Resumen contratos cancelados
        System.out.println("\n📊 Resumen de contratos CANCELADOS:");
        pagoService.resumenContratosCancelados(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31)
        ).forEach(r -> {
            System.out.println(" - Servicio: " + r.getTipoServicio() +
                    ", Cantidad: " + r.getCantidadContratos() +
                    ", Monto total: $" + r.getMontoTotalCobrado());
        });
        // Resumen financiero de contratos no cancelados
        System.out.println("\n💰 Resumen financiero de contratos ACTIVO o VENCIDO:");
        pagoService.obtenerResumenFinancieroContratosNoCancelados().forEach(r -> {
            System.out.println(" - ID: " + r.getIdContrato() +
                    ", Cliente: " + r.getNombreCliente() +
                    ", Servicio: " + r.getTipoServicio() +
                    ", Esperado: $" + r.getMontoEsperado() +
                    ", Pagado: $" + r.getMontoPagado());
        });
        em.close();
    }
}
