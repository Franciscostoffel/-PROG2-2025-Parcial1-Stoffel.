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
        ContratoServicio c1 = new ContratoServicio(null, "Juan Perez", TipoServicio.AGUA, new BigDecimal("1000"), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 1), Estado.ACTIVO);
        ContratoServicio c2 = new ContratoServicio(null, "Ana Lopez", TipoServicio.ELECTRICIDAD, new BigDecimal("1500"), LocalDate.of(2024, 2, 1), LocalDate.of(2024, 7, 1), Estado.ACTIVO);
        em.persist(c1);
        em.persist(c2);
        em.getTransaction().commit();
        PagoService pagoService = PagoService.getInstancia();
        pagoService.registrarPago(new PagoDTO(c1.getId(), new BigDecimal("2000")));
        pagoService.registrarPago(new PagoDTO(c2.getId(), new BigDecimal("1500")));
        FiltroContratoDTO filtro = new FiltroContratoDTO();
        filtro.setNombreCliente("Juan");
        System.out.println("Contratos encontrados: " + pagoService.buscarContratos(filtro).size());
        System.out.println("Resumen cancelados: " +
                pagoService.resumenContratosCancelados(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)).size());

        System.out.println("Resumen financiero: " +
                pagoService.obtenerResumenFinancieroContratosNoCancelados().size());
        em.close();
    }
}
