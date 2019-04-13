package hotelAndes.persistencia;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import hotelAndes.negocio.Catalogo;
import hotelAndes.negocio.TipoUsuario;

public class SQLCatalogo {
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
	public SQLCatalogo (PersistenciaHotelAndes ph)
	{
		this.ph = ph;
	}
	
	
	/**
	 * Crea y ejecuta la sentencia SQL para adicionar un tipo de elemento al catologo a la base de datos de HotelAndes
	 * @param pm - El manejador de persistencia
	 * @param idServicio - El identificador del servicio
	 * @param idProducto - El identificador del producto
	 * @return El número de tuplas insertadas
	 */
	public long adicionarCatalogo(PersistenceManager pm, long idServicio, long idProducto) 
	{
        Query q = pm.newQuery(SQL, "INSERT INTO " + ph.darTablaCatalogo() + "(idServicio, idProducto) values (?, ?)");
        q.setParameters(idServicio, idProducto);
        return (long) q.executeUnique();
	}
	
	
	/**
	 * Crea y ejecuta la sentencia SQL para eliminar todos los  elementos del catalogo asociados a un servicio de la base de datos de HotelAndes, por su identificador
	 * @param pm - El manejador de persistencia
	 * @param idServicio - El identificador del servicio
	 * @return EL número de tuplas eliminadas
	 */
	public long eliminarCatalogoPorIdServicio (PersistenceManager pm, long idServicio)
	{
       Query q = pm.newQuery(SQL, "DELETE FROM " + ph.darTablaCatalogo() + " WHERE id = ?");
       q.setParameters(idServicio);
       return (long) q.executeUnique();
	}
	
	/**
	 * Crea y ejecuta la sentencia SQL para eliminar un  elementos del catalogo asociados a un producto de la base de datos de HotelAndes, por su identificador
	 * @param pm - El manejador de persistencia
	 * @param idProducto - El identificador del Producto
	 * @return EL número de tuplas eliminadas
	 */
	public long eliminarCatalogoPorIdProducto (PersistenceManager pm, long idProducto)
	{
       Query q = pm.newQuery(SQL, "DELETE FROM " + ph.darTablaCatalogo() + " WHERE id = ?");
       q.setParameters(idProducto);
       return (long) q.executeUnique();
	}
	
	/**
	 * Crea y ejecuta la sentencia SQL para eliminar todos los  elementos del catalogo asociados a un producto de la base de datos de HotelAndes, por su identificador
	 * @param pm - El manejador de persistencia
	 * @param idServicio - El identificador del servicio
	 * @param idProducto - El identificador del Producto
	 * @return EL número de tuplas eliminadas
	 */
	public long eliminarCatalogoPorIds (PersistenceManager pm, long idServicio, long idProducto)
	{
       Query q = pm.newQuery(SQL, "DELETE FROM " + ph.darTablaCatalogo() + " WHERE idServicio = ? AND idProducto = ?");
       q.setParameters(idServicio, idProducto);
       return (long) q.executeUnique();
	}
	
	
	/**
	 * Crea y ejecuta la sentencia SQL para encontrar la información de UN Catalogo de la 
	 * base de datos de HotelAndes, por su identificador
	 * @param pm - El manejador de persistencia
	 * @param idServicio - El identificador del servicio
	 * @return El objeto Tipo de Catalogo que tiene el identificador dado
	 */
	public Catalogo darTipoUsuarioPorIdSubasta (PersistenceManager pm, long idServicio) 
	{
		Query q = pm.newQuery(SQL, "SELECT * FROM " + ph.darTablaCatalogo () + " WHERE id = ?");
		q.setResultClass(Catalogo.class);
		q.setParameters(idServicio);
		return (Catalogo) q.executeUnique();
	}
	
	/**
	 * Crea y ejecuta la sentencia SQL para encontrar la información de UN Catalogo de la 
	 * base de datos de HotelAndes, por su identificador
	 * @param pm - El manejador de persistencia
	 * @param idProducto - El identificador del Producto
	 * @return El objeto Tipo de Catalogo que tiene el identificador dado
	 */
	public Catalogo darTipoUsuarioPorIdProducto (PersistenceManager pm, long idProducto) 
	{
		Query q = pm.newQuery(SQL, "SELECT * FROM " + ph.darTablaCatalogo () + " WHERE id = ?");
		q.setResultClass(Catalogo.class);
		q.setParameters(idProducto);
		return (Catalogo) q.executeUnique();
	}
	
	/**
	 * Crea y ejecuta la sentencia SQL para encontrar la información de UN Catalogo de la 
	 * base de datos de HotelAndes, por su identificador
	 * @param pm - El manejador de persistencia
	 * @param idServicio - El identificador del servicio
	 * @param idProducto - El identificador del Producto
	 * @return El objeto Tipo de Catalogo que tiene el identificador dado
	 */
	public Catalogo darTipoUsuarioPorIds (PersistenceManager pm, long idServicio, long idProducto) 
	{
		Query q = pm.newQuery(SQL, "SELECT * FROM " + ph.darTablaCatalogo () + " WHERE idServicio = ? AND idProducto = ?");
		q.setResultClass(Catalogo.class);
		q.setParameters(idServicio, idProducto);
		return (Catalogo) q.executeUnique();
	}

}