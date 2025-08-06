
import org.example.dto.EstadoContratoDTO;
import org.example.dto.PagoDTO;
import org.example.dto.ResumenFinancieroDTO;
import jakarta.persistence.EntityManager;
import org.example.models.ContratoServicio;
import org.example.enums.TipoServicio;
import org.example.enums.Estado;
import org.junit.jupiter.api.*;
import org.example.service.PagoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PagoServiceTest {

    private static EntityManager em;
    private static PagoService service;
    private static Long idContratoTest;

    @BeforeAll
    public static void setup() {
        em = org.example.utils.HibernateUtil.getSession().unwrap(EntityManager.class);
        service = PagoService.getInstancia();
        service.setEntityManager(em);
    }

    @Test
    @Order(1)
    public void testRegistrarPagoYCambiarEstadoACancelado() {
        ContratoServicio contrato = new ContratoServicio(null, "Test Cliente", TipoServicio.GAS,
                new BigDecimal("1000"), LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 4, 1), Estado.ACTIVO);

        em.getTransaction().begin();
        em.persist(contrato);
        em.getTransaction().commit();
        idContratoTest = contrato.getId();

        // Total esperado: 3 meses * 1000 = 3000
        EstadoContratoDTO resultado = service.registrarPago(new PagoDTO(idContratoTest, new BigDecimal("3000")));
        assertEquals(Estado.CANCELADO, resultado.getEstadoActualizado());
    }

    @Test
    @Order(2)
    public void testResumenFinancieroIncluyeContratoVencido() {
        em.getTransaction().begin();
        ContratoServicio contrato = em.find(ContratoServicio.class, idContratoTest);
        contrato.setFechaFin(LocalDate.of(2024, 3, 1));
        contrato.setEstado(Estado.VENCIDO);
        em.merge(contrato);
        em.getTransaction().commit();

        List<ResumenFinancieroDTO> resumen = service.obtenerResumenFinancieroContratosNoCancelados();
        assertNotNull(resumen, "El resumen financiero no debe ser null");
        assertTrue(resumen.stream().anyMatch(r -> r.getIdContrato().equals(idContratoTest)));
    }

    @Test
    @Order(3)
    public void testRegistrarPagoContratoInexistente_conMock() {
        PagoService mockService = PagoService.getInstancia();
        EntityManager emMock = mock(EntityManager.class);
        mockService.setEntityManager(emMock);

        when(emMock.find(ContratoServicio.class, 999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mockService.registrarPago(new PagoDTO(999L, new BigDecimal("1000")));
        });

        assertTrue(exception.getMessage().contains("Contrato no encontrado"));
    }
}

