package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.dto.ContratoDTO;
import org.example.dto.EstadoContratoDTO;
import org.example.dto.FiltroContratoDTO;
import org.example.dto.PagoDTO;
import org.example.enums.Estado;
import org.example.models.Contrato;
import org.example.models.Pago;
import org.example.utils.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PagoService {
    private static PagoService instancia;

    private PagoService() {}

    public static PagoService getInstancia() {
        if (instancia == null) {
            instancia = new PagoService();
        }
        return instancia;
    }
    public EstadoContratoDTO registrarPago(PagoDTO dto) {
        EntityManager em = HibernateUtil.getSession().unwrap(EntityManager.class);

        Contrato contrato = em.find(Contrato.class, dto.getIdContrato());

        if (contrato == null) {
            throw new RuntimeException("Contrato no encontrado con ID: " + dto.getIdContrato());
        }
        Pago nuevoPago = new Pago();
        nuevoPago.setContrato(contrato);
        nuevoPago.setFechaPago(LocalDate.now());
        nuevoPago.setMonto(dto.getMonto());

        em.getTransaction().begin();
        em.persist(nuevoPago);

        long meses = ChronoUnit.MONTHS.between(contrato.getFechaInicio(), contrato.getFechaFin());
        if (meses == 0) meses = 1; // mínimo 1 mes
        BigDecimal totalEsperado = contrato.getTarifaMensual().multiply(BigDecimal.valueOf(meses));

        TypedQuery<BigDecimal> query = em.createQuery(
                "SELECT COALESCE(SUM(p.monto), 0) FROM PagoServicio p WHERE p.contrato.id = :id",
                BigDecimal.class
        );
        query.setParameter("id", contrato.getId());
        BigDecimal totalPagado = query.getSingleResult();

        Estado nuevoEstado = contrato.getEstado();
        if (totalPagado.compareTo(totalEsperado) >= 0) {
            nuevoEstado = Estado.CANCELADO;
        } else if (contrato.getFechaFin().isBefore(LocalDate.now())) {
            nuevoEstado = Estado.VENCIDO;
        } else {
            nuevoEstado = Estado.ACTIVO;
        }

        contrato.setEstado(nuevoEstado);
        em.merge(contrato);
        em.getTransaction().commit();

        return new EstadoContratoDTO(contrato.getId(), totalPagado, nuevoEstado);
    }
    public List<ContratoDTO> buscarContratos(FiltroContratoDTO filtro) {
        EntityManager em = HibernateUtil.getSession().unwrap(EntityManager.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Contrato> cq = cb.createQuery(Contrato.class);
        Root<Contrato> root = cq.from(Contrato.class);

        List<Predicate> predicados = new ArrayList<>();
        // Obligatorio
        predicados.add(cb.like(cb.lower(root.get("nombreCliente")), "%" + filtro.getNombreCliente().toLowerCase() + "%"));
        // Opcional
        if (filtro.getTipoServicio() != null) {
            predicados.add(cb.equal(root.get("tipoServicio"), Enum.valueOf(org.example.enums.TipoServicio.class, filtro.getTipoServicio())));
        }
        // Opcional
        if (filtro.getFechaInicioDesde() != null) {
            predicados.add(cb.greaterThanOrEqualTo(root.get("fechaInicio"), filtro.getFechaInicioDesde()));
        }
        if (filtro.getFechaInicioHasta() != null) {
            predicados.add(cb.lessThanOrEqualTo(root.get("fechaInicio"), filtro.getFechaInicioHasta()));
        }

        // Opcional: Rango de tarifas
        if (filtro.getTarifaDesde() != null) {
            predicados.add(cb.greaterThanOrEqualTo(root.get("tarifaMensual"), filtro.getTarifaDesde()));
        }
        if (filtro.getTarifaHasta() != null) {
            predicados.add(cb.lessThanOrEqualTo(root.get("tarifaMensual"), filtro.getTarifaHasta()));
        }

        cq.select(root)
                .where(cb.and(predicados.toArray(new Predicate[0])))
                .orderBy(cb.desc(root.get("fechaInicio")));

        List<Contrato> resultados = em.createQuery(cq).getResultList();
        return resultados.stream().map(c -> new ContratoDTO(
                c.getId(),
                c.getNombreCliente(),
                c.getTipoServicio().name(),
                c.getTarifaMensual(),
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getEstado().name()
        )).toList();
    }

}

