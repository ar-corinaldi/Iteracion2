package hotelAndes.persistencia;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import hotelAndes.negocio.Habitacion;
import hotelAndes.negocio.TipoHabitacion;


public class SQLTipoHabitacion {
	/* ****************************************************************
	 * 			Constantes
	 *****************************************************************/
	/**
	 * Cadena que representa el tipo de consulta que se va a realizar en las sentencias de acceso a la base de datos
	 * Se renombra ac� para facilitar la escritura de las sentencias
	 */
	private final static String SQL = PersistenciaHotelAndes.SQL;

	/* ****************************************************************
	 * 			Atributos
	 *****************************************************************/
	/**
	 * El manejador de persistencia general de la aplicaci�n
	 */
	private PersistenciaHotelAndes ph;

	/* ****************************************************************
	 * 			M�todos
	 *****************************************************************/
	
	/**
	 * Constructor
	 * @param pp - El Manejador de persistencia de la aplicaci�n
	 */
	public SQLTipoHabitacion (PersistenciaHotelAndes ph)
	{
		this.ph = ph;
	}
	
	
	/**
	 * Crea y ejecuta la sentencia SQL para adicionar un tipo de Habitacion a la base de datos de HotelAndes
	 * @param pm - El manejador de persistencia
	 * @param id - El identificador del tipo de Habitacion
	 * @param nombre - El nombre del tipo de Habitacion
	 * @param costo - El costo del tipo de Habitacion
	 * @param capacidad - La capacidad del tipo de Habitacion
	 * @return El n�mero de tuplas insertadas
	 */
	public long adicionarTipoHabitacion (PersistenceManager pm, long id, String nombre, double costo, int capacidad) 
	{
        Query q = pm.newQuery(SQL, "INSERT INTO " + ph.darTablaTipoHabitacion() + "(id, nombre, costo, capacidad) values (?, ?, ?, ?)");
        q.setParameters(id, nombre, costo, capacidad);
        return (long) q.executeUnique();
	}
	
	
	/**
	 * Crea y ejecuta la sentencia SQL para eliminar un tipo de Habitacion de la base de datos de HotelAndes, por su identificador
	 * @param pm - El manejador de persistencia
	 * @param id - El identificador del tipo de Habitacion
	 * @return EL n�mero de tuplas eliminadas
	 */
	public long eliminarTipoHabitacionPorId (PersistenceManager pm, long id)
	{
       Query q = pm.newQuery(SQL, "DELETE FROM " + ph.darTablaTipoHabitacion() + " WHERE id = ?");
       q.setParameters(id);
       return (long) q.executeUnique();
	}
	
	/**
	 * Crea y ejecuta la sentencia SQL para encontrar la informaci�n de UN Tipo de Habitacion de la 
	 * base de datos de HotelAndes, por su identificador
	 * @param pm - El manejador de persistencia
	 * @param id - El identificador del Tipo de Habitacion
	 * @return El objeto Tipo de Habitacion que tiene el identificador dado
	 */
	public TipoHabitacion darTipoHabitacionPorId (PersistenceManager pm, long id) 
	{
		Query q = pm.newQuery(SQL, "SELECT * FROM " + ph.darTablaTipoHabitacion () + " WHERE id = ?");
		q.setResultClass(TipoHabitacion.class);
		q.setParameters(id);
		return (TipoHabitacion) q.executeUnique();
	}


	public List<TipoHabitacion> darTiposHabitacion(PersistenceManager pm){
		Query q = pm.newQuery(SQL, "SELECT * FROM " + ph.darTablaTipoHabitacion());
		q.setResultClass(TipoHabitacion.class);
		return (List<TipoHabitacion>) q.executeList();
	}

}