package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.example.dto.*;
import org.example.enums.Estado;
import org.example.enums.TipoServicio;
import org.example.models.ContratoServicio;
import org.example.models.PagoServicio;
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

        ContratoServicio contrato = em.find(ContratoServicio.class, dto.getIdContrato());

        if (contrato == null) {
            throw new RuntimeException("Contrato no encontrado con ID: " + dto.getIdContrato());
        }
        PagoServicio nuevoPago = new PagoServicio();
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
        CriteriaQuery<ContratoServicio> cq = cb.createQuery(ContratoServicio.class);
        Root<ContratoServicio> root = cq.from(ContratoServicio.class);

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

        List<ContratoServicio> resultados = em.createQuery(cq).getResultList();
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

    public List<ResumenCanceladosDTO> resumenContratosCancelados(LocalDate desde, LocalDate hasta) {
        EntityManager em = HibernateUtil.getSession().unwrap(EntityManager.class);

        String jpql = "SELECT c.tipoServicio, COUNT(c), SUM(c.tarifaMensual * TIMESTAMPDIFF(MONTH, c.fechaInicio, c.fechaFin)) " +
                "FROM ContratoServicio c " +
                "WHERE c.estado = :estado " +
                "AND c.fechaInicio BETWEEN :desde AND :hasta " +
                "GROUP BY c.tipoServicio";

        List<Object[]> resultados = em.createQuery(jpql, Object[].class)
                .setParameter("estado", Estado.CANCELADO)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();

        List<ResumenCanceladosDTO> resumen = new ArrayList<>();

        for (Object[] fila : resultados) {
            TipoServicio tipoServicio = TipoServicio.valueOf(((TipoServicio) fila[0]).name());
            Long cantidad = (Long) fila[1];
            BigDecimal montoTotal = new BigDecimal(fila[2].toString());
            resumen.add(new ResumenCanceladosDTO(tipoServicio, cantidad, montoTotal));
        }
        return resumen;
    }

    public List<ResumenFinancieroDTO> obtenerResumenFinancieroContratosNoCancelados() {
        EntityManager em = HibernateUtil.getSession().unwrap(EntityManager.class);

        TypedQuery<ContratoServicio> query = em.createQuery(
                "SELECT c FROM ContratoServicio c WHERE c.estado IN (:estado1, :estado2)",
                ContratoServicio.class
        );
        query.setParameter("estado1", Estado.ACTIVO);
        query.setParameter("estado2", Estado.VENCIDO);

        List<ContratoServicio> contratos = query.getResultList();
        List<ResumenFinancieroDTO> resumen = new ArrayList<>();

        for (ContratoServicio contrato : contratos) {
            // Calcular meses contratados
            long meses = ChronoUnit.MONTHS.between(contrato.getFechaInicio(), contrato.getFechaFin());
            if (meses == 0) meses = 1;
            BigDecimal totalEsperado = contrato.getTarifaMensual().multiply(BigDecimal.valueOf(meses));

            TypedQuery<BigDecimal> pagoQuery = em.createQuery(
                    "SELECT COALESCE(SUM(p.monto), 0) FROM PagoServicio p WHERE p.contrato.id = :id",
                    BigDecimal.class
            );
            pagoQuery.setParameter("id", contrato.getId());
            BigDecimal totalPagado = pagoQuery.getSingleResult();

            resumen.add(new ResumenFinancieroDTO(
                    contrato.getId(),
                    contrato.getNombreCliente(),
                    contrato.getTipoServicio().name(),
                    totalEsperado,
                    totalPagado
            ));
        }
        return resumen;
    }

    public void setEntityManager(EntityManager emMock) {
    }
}

