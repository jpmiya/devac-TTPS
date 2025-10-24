package org.example.devac.DAOs;

import org.example.devac.models.Mascota;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test simple para verificar persistencia básica de Mascota.
 * Nota: el modelo `Mascota` en el repo no expone setters para la mayoría
 * de campos, por lo que este test valida que la entidad se persiste y
 * puede recuperarse por id.
 */
public class MascotaDAOTest {

	private final MascotaDAOHibernateJPA mascotaDao = new MascotaDAOHibernateJPA();
	private Long createdId;

	@AfterEach
	public void cleanup() {
		if (createdId != null) {
			try {
				mascotaDao.delete(createdId);
			} catch (Exception ignored) {}
			createdId = null;
		}
	}

	@Test
	public void persist_and_getById_shouldReturnPersistedMascota() {
		Mascota m = new Mascota();

		Mascota created = mascotaDao.persist(m);
		assertNotNull(created);
		// obtener id generado
		createdId = created.getId();
		assertNotNull(createdId, "El id debe asignarse al persistir");

		Mascota found = mascotaDao.get(createdId);
		assertNotNull(found, "La mascota persistida debe poder recuperarse por id");
	}
}
