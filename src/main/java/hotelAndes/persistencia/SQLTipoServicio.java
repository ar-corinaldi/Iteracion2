package hotelAndes.persistencia;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import hotelAndes.negocio.Habitacion;
import hotelAndes.negocio.TipoServicio;

public class SQLTipoServicio {

	/* ****************************************************************
	 * 			Constantes
	 *****************************************************************/
	/**
	 * Cadena que representa el tipo de consulta que se va a realizar en las sentencias de acceso a la base de datos
	 * Se renombra acá para facilitar la escritura de las sentencias
	 */
	private final static String SQL = PersistenciaHotelAndes.SQL;

	/* ****************************************************************
	 * 			Atributos
	 *****************************************************************/
	/**
	 * El manejador de persistencia general de la aplicación
	 */
	private PersistenciaHotelAndes ph;

	/* ****************************************************************
	 * 			Métodos
	 *****************************************************************/
	
	/**
	 * Constructor
	 * @param pp - El Manejador de persistencia de la aplicación
	 */
	public SQLTipoServicio (PersistenciaHotelAndes ph)
	{
		this.ph = ph;
	}
	
	
	/**
	 * Crea y ejecuta la sentencia SQL para adicionar un tipo de Servicio a la base de datos de HotelAndes
	 * @param pm - El manejador de persistencia
	 * @param id - El identificador del tipo de Servicio
	 * @param nombre - El nombre del tipo de Servicio
	 * @return El número de tuplas insertadas
	 */
	public long adicionarTipoServicio (PersistenceManager pm, long id, String nombre) 
	{
        Query q = pm.newQuery(SQL, "INSERT INTO " + ph.darTablaTipoServicio() + "(id, nombre) values (?, ?)");
        q.setParameters(id, nombre);
        return (long) q.executeUnique();
	}
	
	
	/**
	 * Crea y ejecuta la sentencia SQL para eliminar un tipo de Servicio de la base de datos de HotelAndes, por su identificador
	 * @param pm - El manejador de persistencia
	 * @param id - El identificador del tipo de Servicio
	 * @return EL número de tuplas eliminadas
	 */
	public long eliminarTipoServicioPorId (PersistenceManager pm, long id)
	{
       Query q = pm.newQuery(SQL, "DELETE FROM " + ph.darTablaTipoServicio() + " WHERE id = ?");
       q.setParameters(id);
       return (long) q.executeUnique();
	}
	
	/**
	 * Crea y ejecuta la sentencia SQL para encontrar la información de UN Tipo de Servicio de la 
	 * base de datos de HotelAndes, por su identificador
	 * @param pm - El manejador de persistencia
	 * @param id - El identificador del Tipo de Servicio
	 * @return El objeto Tipo de Servicio que tiene el identificador dado
	 */
	public TipoServicio darTipoServicioPorId (PersistenceManager pm, long id) 
	{
		Query q = pm.newQuery(SQL, "SELECT * FROM " + ph.darTablaTipoServicio () + " WHERE id = ?");
		q.setResultClass(TipoServicio.class);
		q.setParameters(id);
		return (TipoServicio) q.executeUnique();
	}


	public List<TipoServicio> darTiposServicio(PersistenceManager pm){
		Query q = pm.newQuery(SQL, "SELECT * FROM " + ph.darTablaTipoServicio());
		q.setResultClass(TipoServicio.class);
		return (List<TipoServicio>) q.executeList();
	}
}