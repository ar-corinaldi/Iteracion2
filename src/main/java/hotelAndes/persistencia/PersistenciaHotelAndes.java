package hotelAndes.persistencia;

import hotelAndes.negocio.Check;
import hotelAndes.negocio.Consumo;
import hotelAndes.negocio.Habitacion;
import hotelAndes.negocio.PlanConsumo;
import hotelAndes.negocio.Reserva;
import hotelAndes.negocio.Servicio;
import hotelAndes.negocio.TipoHabitacion;
import hotelAndes.negocio.TipoPlanConsumo;
import hotelAndes.negocio.TipoServicio;
import hotelAndes.negocio.TipoUsuario;
import hotelAndes.negocio.Usuario;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.JDODataStoreException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;





import javax.jdo.Transaction;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class PersistenciaHotelAndes {

	/******************************************************************************
	 * CONSTANTES
	 ******************************************************************************/

	private static Logger log = Logger.getLogger(PersistenciaHotelAndes.class.getName());

	/**
	 * Cadena para indicar el tipo de sentencias que se va a utilizar en una consulta
	 */
	public final static String SQL = "javax.jdo.query.SQL";

	/******************************************************************************
	 * ATRIBUTOS
	 ******************************************************************************/

	/**
	 * Atributo privado que es el �nico objeto de la clase - Patr�n SINGLETON
	 */
	private static PersistenciaHotelAndes instance;

	/**
	 * F�brica de Manejadores de persistencia, para el manejo correcto de las transacciones
	 */
	private PersistenceManagerFactory pmf;

	/**
	 * Arreglo de cadenas con los nombres de las tablas de la base de datos, en su orden:
	 * Secuenciador, tipoBebida, bebida, bar, bebedor, gustan, sirven y visitan
	 */
	private List <String> tablas;

	/**
	 * Atributo para el acceso a la tabla Catalogo de la base de datos
	 */
	private SQLCatalogo sqlCatalogo;

	/**
	 * Atributo para el acceso a la tabla Consumo de la base de datos
	 */
	private SQLConsumo sqlConsumo;

	/**
	 * Atributo para el acceso a la tabla Habitacion de la base de datos
	 */
	private SQLHabitacion sqlHabitacion;

	/**
	 * Atributo para el acceso a la tabla Hotel de la base de datos
	 */
	private SQLHotel sqlHotel;

	/**
	 * Atributo para el acceso a la tabla PlanConsumo de la base de datos
	 */
	private SQLPlanConsumo sqlPlanConsumo;

	/**
	 * Atributo para el acceso a la tabla Reserva de la base de datos
	 */
	private SQLReserva sqlReserva;

	/**
	 * Atributo para el acceso a la tabla Servicio de la base de datos
	 */
	private SQLServicio sqlServicio;

	/**
	 * Atributo para el acceso a la tabla TipoHabitacion de la base de datos
	 */
	private SQLTipoHabitacion sqlTipoHabitacion;

	/**
	 * Atributo para el acceso a la tabla TipoPlanConsumo de la base de datos
	 */
	private SQLTipoPlanConsumo sqlTipoPlanConsumo;

	/**
	 * Atributo para el acceso a la tabla TipoUsuario de la base de datos
	 */
	private SQLTipoUsuario sqlTipoUsuario;

	/**
	 * Atributo para el acceso a la tabla TipoServicio de la base de datos
	 */
	private SQLTipoServicio sqlTipoServicio;


	/**
	 * Atributo para el acceso a la tabla Usuario de la base de datos
	 */
	private SQLUsuario sqlUsuario;

	/**
	 * Atributo para el acceso a las sentencias SQL propias a PersistenciaHotelAndes
	 */
	private SQLUtil sqlUtil;

	/**
	 * Atributo para el acceso a la tabla check de la base de datos
	 */
	private SQLCheck sqlCheck;

	/******************************************************************************
	 * ATRIBUTOS
	 ******************************************************************************/

	/**
	 * Constructor privado con valores por defecto - Patr�n SINGLETON
	 */
	private PersistenciaHotelAndes()
	{
		pmf = JDOHelper.getPersistenceManagerFactory("HotelAndes");		
		crearClasesSQL ();

		// Define los nombres por defecto de las tablas de la base de datos
		tablas = new LinkedList<String> ();
		tablas.add ("Hotel_sequence"); //No se que poner
		tablas.add("CATALOGO");
		tablas.add ("CONSUMOS");
		tablas.add ("HABITACIONES");
		tablas.add ("HOTELES");
		tablas.add ("PLANES_DE_CONSUMO");
		tablas.add ("PRODUCTOS");
		tablas.add ("RESERVAS");
		tablas.add ("SERVICIOS");
		tablas.add ("TIPO_HABITACIONES");
		tablas.add ("TIPO_PLANES_DE_CONSUMO");
		tablas.add("TIPO_SERVICIOS");
		tablas.add ("TIPO_USUARIOS");
		tablas.add ("USUARIOS");
		tablas.add("CHECK");

	}

	/**
	 * Constructor privado, que recibe los nombres de las tablas en un objeto Json - Patr�n SINGLETON
	 * @param tableConfig - Objeto Json que contiene los nombres de las tablas y de la unidad de persistencia a manejar
	 */
	private PersistenciaHotelAndes(JsonObject tableConfig)
	{
		crearClasesSQL ();
		tablas = leerNombresTablas (tableConfig);

		String unidadPersistencia = tableConfig.get ("unidadPersistencia").getAsString ();
		log.trace("Accediendo unidad de persistencia: " + unidadPersistencia);
		System.out.println(unidadPersistencia);
		pmf = JDOHelper.getPersistenceManagerFactory (unidadPersistencia);
	}

	/**
	 * @return Retorna el �nico objeto PersistenciaHotelAndes existente - Patr�n SINGLETON
	 */
	public static PersistenciaHotelAndes getInstance ()
	{
		if (instance == null)
		{
			instance = new PersistenciaHotelAndes ();
		}
		return instance;
	}

	/**
	 * Constructor que toma los nombres de las tablas de la base de datos del objeto tableConfig
	 * @param tableConfig - El objeto JSON con los nombres de las tablas
	 * @return Retorna el �nico objeto PersistenciaHotelAndes existente - Patr�n SINGLETON
	 */
	public static PersistenciaHotelAndes getInstance (JsonObject tableConfig)
	{
		if (instance == null)
		{
			instance = new PersistenciaHotelAndes (tableConfig);
		}
		return instance;
	}

	/**
	 * Cierra la conexi�n con la base de datos
	 */
	public void cerrarUnidadPersistencia ()
	{
		pmf.close ();
		instance = null;
	}

	/**
	 * Genera una lista con los nombres de las tablas de la base de datos
	 * @param tableConfig - El objeto Json con los nombres de las tablas
	 * @return La lista con los nombres del secuenciador y de las tablas
	 */
	private List <String> leerNombresTablas (JsonObject tableConfig)
	{
		JsonArray nombres = tableConfig.getAsJsonArray("tablas") ;

		List <String> resp = new LinkedList <String> ();
		for (JsonElement nom : nombres)
		{
			resp.add (nom.getAsString ());
		}

		return resp;
	}

	/**
	 * Crea los atributos de clases de apoyo SQL
	 */
	private void crearClasesSQL ()
	{
		sqlConsumo = new SQLConsumo(this);
		sqlHabitacion = new SQLHabitacion(this);
		sqlHotel = new SQLHotel(this);
		sqlPlanConsumo = new SQLPlanConsumo (this);
		sqlReserva = new SQLReserva(this);		
		sqlServicio = new SQLServicio(this);
		sqlTipoHabitacion = new SQLTipoHabitacion(this);
		sqlTipoPlanConsumo = new SQLTipoPlanConsumo(this);		
		sqlTipoUsuario = new SQLTipoUsuario(this);
		sqlTipoServicio = new SQLTipoServicio(this);
		sqlUsuario = new SQLUsuario(this);
		sqlCatalogo = new SQLCatalogo(this);
		sqlCheck = new SQLCheck(this);

		sqlUtil = new SQLUtil(this);
	}

	/**
	 * @return La cadena de caracteres con el nombre del secuenciador de Hotelandes
	 */
	public String darSeqHotelAndes() {
		return tablas.get(0);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de Catalogo de Hotelandes
	 */
	public String darTablaCatalogo() {
		return tablas.get(1);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de Consumo de Hotelandes
	 */
	public String darTablaConsumo() {
		return tablas.get(2);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de Habitacion de Hotelandes
	 */
	public String darTablaHabitacion() {
		return tablas.get(3);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de Hotel de Hotelandes
	 */
	public String darTablaHotel()
	{
		return tablas.get(4);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de PlanConsumo de Hotelandes
	 */
	public String darTablaPlanConsumo() {
		return tablas.get(5);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de Producto de Hotelandes
	 */
	public String darTablaProducto ()
	{
		return tablas.get(6);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de Reserva de Hotelandes
	 */
	public String darTablaReserva() {
		return tablas.get(7);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de Servicio de Hotelandes
	 */
	public String darTablaServicio() {
		return tablas.get(8);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de TipoHabitacion de Hotelandes
	 */
	public String darTablaTipoHabitacion() {
		return tablas.get(9);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de TipoPlanConsumo de Hotelandes
	 */
	public String darTablaTipoPlanConsumo() {
		return tablas.get(10);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla TipoServicio de HotelAndes
	 */
	public String darTablaTipoServicio(){
		return tablas.get(11);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla TipoUsuario de HotelAndes
	 */
	public String darTablaTipoUsuario(){
		return tablas.get(12);
	}

	/**
	 * @return La cadena de caracteres con el nombre de la tabla de Usuario de Hotelandes
	 */
	public String darTablaUsuario() {
		return tablas.get(13);
	}
	
	/**
	 * @return La cadena de caracteres con el nombre de la tabla de Check de Hotelandes
	 */
	public String darTablaCheck() {
		return tablas.get(14);
	}

	/**
	 * Transacci�n para el generador de secuencia de HotelAndes
	 * Adiciona entradas al log de la aplicaci�n
	 * @return El siguiente n�mero del secuenciador de HotelAndes
	 */
	private long nextval ()
	{
		long resp = sqlUtil.nextval(pmf.getPersistenceManager());
		log.trace ("Generando secuencia: " + resp);
		return resp;
	}

	/**
	 * Extrae el mensaje de la exception JDODataStoreException embebido en la Exception e, que da el detalle espec�fico del problema encontrado
	 * @param e - La excepci�n que ocurrio
	 * @return El mensaje de la excepci�n JDO
	 */
	private String darDetalleException(Exception e) 
	{
		String resp = "";
		if (e.getClass().getName().equals("javax.jdo.JDODataStoreException"))
		{
			JDODataStoreException je = (javax.jdo.JDODataStoreException) e;
			return je.getNestedExceptions() [0].getMessage();
		}
		return resp;
	}
	
	/* ****************************************************************
	 * 			Métodos para manejar los TIPO_HABITACION
	 *****************************************************************/
	
	/**
	 * Método que inserta, de manera transaccional, una tupla en la tabla GUSTAN
	 * Adiciona entradas al log de la aplicación
	 * @param idBebedor - El identificador del bebedor - Debe haber un bebedor con ese identificador
	 * @param idBebida - El identificador de la bebida - Debe haber una bebida con ese identificador
	 * @return Un objeto GUSTAN con la información dada. Null si ocurre alguna Excepción
	 */
	public TipoHabitacion adicionarTipoHabitacion(long id, String nombre, double costo, int capacidad) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long tuplasInsertadas = sqlTipoHabitacion.adicionarTipoHabitacion(pm, id, nombre, costo, capacidad);
            tx.commit();

            return new TipoHabitacion(id, nombre, costo, capacidad);
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
        	return null;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
	
	/**
	 * Método que elimina, de manera transaccional, una tupla en la tabla GUSTAN, dados los identificadores de bebedor y bebida
	 * @param idBebedor - El identificador del bebedor
	 * @param idBebida - El identificador de la bebida
	 * @return El número de tuplas eliminadas. -1 si ocurre alguna Excepción
	 */
	public long eliminarTipoHabitacion(long id) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long resp = sqlTipoHabitacion.eliminarTipoHabitacionPorId(pm, id)  ;      
            tx.commit();

            return resp;
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
        	return -1;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
	
	
	public TipoHabitacion darTipoHabitacion(long idTipo) {
		return sqlTipoHabitacion.darTipoHabitacionPorId(pmf.getPersistenceManager(), idTipo);
	}
	
	/**
	 * Método que consulta todas las tuplas en la tabla GUSTAN
	 * @return La lista de objetos GUSTAN, construidos con base en las tuplas de la tabla GUSTAN
	 */
	public List<TipoHabitacion> darTipoHabitacion()
	{
		return sqlTipoHabitacion.darTiposHabitacion(pmf.getPersistenceManager());
	}
	
	/* ****************************************************************
	 * 			Métodos para manejar los TIPO_PLAN_CONSUMO
	 *****************************************************************/
	/**
	 * Método que inserta, de manera transaccional, una tupla en la tabla GUSTAN
	 * Adiciona entradas al log de la aplicación
	 * @param idBebedor - El identificador del bebedor - Debe haber un bebedor con ese identificador
	 * @param idBebida - El identificador de la bebida - Debe haber una bebida con ese identificador
	 * @return Un objeto GUSTAN con la información dada. Null si ocurre alguna Excepción
	 */
	public TipoPlanConsumo adicionarTipoPlanConsumo(long id, String nombre) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long tuplasInsertadas = sqlTipoPlanConsumo.adicionarTipoPlanConsumo(pm, id, nombre);
            tx.commit();

            return new TipoPlanConsumo(id, nombre);
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
        	return null;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
	
	/**
	 * Método que elimina, de manera transaccional, una tupla en la tabla GUSTAN, dados los identificadores de bebedor y bebida
	 * @param idBebedor - El identificador del bebedor
	 * @param idBebida - El identificador de la bebida
	 * @return El número de tuplas eliminadas. -1 si ocurre alguna Excepción
	 */
	public long eliminarTipoPlanConsumo(long id) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long resp = sqlTipoPlanConsumo.eliminarTipoPlanConsumoPorId(pm, id)  ;      
            tx.commit();

            return resp;
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
        	return -1;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
	
	/**
	 * Método que consulta todas las tuplas en la tabla GUSTAN
	 * @return La lista de objetos GUSTAN, construidos con base en las tuplas de la tabla GUSTAN
	 */
	public List<PlanConsumo> darTipoPlanConsumo()
	{
		return sqlTipoPlanConsumo.darTiposPlanConsumo(pmf.getPersistenceManager());
	}
	
	/* ****************************************************************
	 * 			Métodos para manejar los TIPO_SERVICIO
	 *****************************************************************/
	/**
	 * Método que inserta, de manera transaccional, una tupla en la tabla GUSTAN
	 * Adiciona entradas al log de la aplicación
	 * @param idBebedor - El identificador del bebedor - Debe haber un bebedor con ese identificador
	 * @param idBebida - El identificador de la bebida - Debe haber una bebida con ese identificador
	 * @return Un objeto GUSTAN con la información dada. Null si ocurre alguna Excepción
	 */
	public TipoServicio adicionarTipoServicio(long id, String nombre) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long tuplasInsertadas = sqlTipoServicio.adicionarTipoServicio(pm, id, nombre);
            tx.commit();

            return new TipoServicio(id, nombre);
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
        	return null;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
	
	/**
	 * Método que elimina, de manera transaccional, una tupla en la tabla GUSTAN, dados los identificadores de bebedor y bebida
	 * @param idBebedor - El identificador del bebedor
	 * @param idBebida - El identificador de la bebida
	 * @return El número de tuplas eliminadas. -1 si ocurre alguna Excepción
	 */
	public long eliminarTipoServicio(long id) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long resp = sqlTipoServicio.eliminarTipoServicioPorId(pm, id)  ;      
            tx.commit();

            return resp;
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
        	return -1;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
	
	/**
	 * Método que consulta todas las tuplas en la tabla GUSTAN
	 * @return La lista de objetos GUSTAN, construidos con base en las tuplas de la tabla GUSTAN
	 */
	public List<TipoServicio> darTiposServicio()
	{
		return sqlTipoServicio.darTiposServicio(pmf.getPersistenceManager());
	}
	
	/* ****************************************************************
	 * 			Métodos para manejar los TIPO_USUARIO
	 *****************************************************************/
	
	/**
	 * Método que inserta, de manera transaccional, una tupla en la tabla GUSTAN
	 * Adiciona entradas al log de la aplicación
	 * @param idBebedor - El identificador del bebedor - Debe haber un bebedor con ese identificador
	 * @param idBebida - El identificador de la bebida - Debe haber una bebida con ese identificador
	 * @return Un objeto GUSTAN con la información dada. Null si ocurre alguna Excepción
	 */
	public TipoUsuario adicionarTipoUsuario(long id, String nombre) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long tuplasInsertadas = sqlTipoUsuario.adicionarTipoUsuario(pm, id, nombre);
            tx.commit();

            return new TipoUsuario(id, nombre);
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
        	return null;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
	
	/**
	 * Método que elimina, de manera transaccional, una tupla en la tabla GUSTAN, dados los identificadores de bebedor y bebida
	 * @param idBebedor - El identificador del bebedor
	 * @param idBebida - El identificador de la bebida
	 * @return El número de tuplas eliminadas. -1 si ocurre alguna Excepción
	 */
	public long eliminarTipoUsuario (long id) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long resp = sqlTipoUsuario .eliminarTipoUsuarioPorId(pm, id)  ;      
            tx.commit();

            return resp;
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
        	return -1;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
	
	/**
	 * Método que consulta todas las tuplas en la tabla GUSTAN
	 * @return La lista de objetos GUSTAN, construidos con base en las tuplas de la tabla GUSTAN
	 */
	public List<TipoUsuario> darTiposUsuario ()
	{
		return sqlTipoUsuario.darTiposUsuario(pmf.getPersistenceManager());
	}
	
	/* ****************************************************************
	 * 			Métodos para manejar los HABITACIONES
	 *****************************************************************/
	
	/**
	 * M�todo que inserta, de manera transaccional, una tupla en la tabla Bebida
	 * Adiciona entradas al log de la aplicaci�n
	 * @param id 
	 * @param fecha 
	 * @param id_usuario 
	 * @param tipo_documento_usuario 
	 * @param id_servicio 
	 * @param id_habitacion 
	 * @param nombre - El nombre de la bebida
	 * @param idTipoBebida - El identificador del tipo de bebida (Debe existir en la tabla TipoBebida)
	 * @param gradoAlcohol - El grado de alcohol de la bebida (mayor que 0)
	 * @return El objeto Bebida adicionado. null si ocurre alguna Excepci�n
	 */

	public Habitacion adicionarHabitacion(long id, double cuenta_habitacion, long tipo_habitacion, long id_hotel) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();            
			long tuplasInsertadas = sqlHabitacion.adicionarHabitacion(pm, id, cuenta_habitacion, tipo_habitacion, id_hotel);
			tx.commit();

			log.trace ("insercionconsumo: " + id + ": " + tuplasInsertadas + " tuplas insertadas");
			Habitacion habitacion = sqlHabitacion.darHabitacionPorId(pm, id);
			
			return  new Habitacion(tipo_habitacion, cuenta_habitacion, id);
		}
		catch (Exception e)
		{
			//        	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
	 * Método que elimina, de manera transaccional, una tupla en la tabla Bebida, dado el identificador de la bebida
	 * Adiciona entradas al log de la aplicación
	 * @param idBebida - El identificador de la bebida
	 * @return El número de tuplas eliminadas. -1 si ocurre alguna Excepción
	 */
	public long eliminarHabitacionPorId (long idHabitacion) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long resp = sqlHabitacion.eliminarHabitacionPorId (pm, idHabitacion);
            tx.commit();

            return resp;
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
            return -1;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
 
	/**
	 * Método que consulta todas las tuplas en la tabla Bebida
	 * @return La lista de objetos Bebida, construidos con base en las tuplas de la tabla BEBIDA
	 */
	public List<Habitacion> darHabitaciones ()
	{
		return sqlHabitacion.darHabitaciones(pmf.getPersistenceManager());
	}
	
	

	

	/* ****************************************************************
	 * 			M�todos para manejar los CONSUMOS
	 *****************************************************************/
	/**
	 * M�todo que inserta, de manera transaccional, una tupla en la tabla Bebida
	 * Adiciona entradas al log de la aplicaci�n
	 * @param id 
	 * @param fecha 
	 * @param id_usuario 
	 * @param tipo_documento_usuario 
	 * @param id_servicio 
	 * @param id_habitacion 
	 * @param nombre - El nombre de la bebida
	 * @param idTipoBebida - El identificador del tipo de bebida (Debe existir en la tabla TipoBebida)
	 * @param gradoAlcohol - El grado de alcohol de la bebida (mayor que 0)
	 * @return El objeto Bebida adicionado. null si ocurre alguna Excepci�n
	 */

	public Consumo adicionarConsumo(long id, Timestamp fecha, long id_usuario, String tipo_documento_usuario, long id_servicio, long id_habitacion) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();            
			long tuplasInsertadas = sqlConsumo.adicionarConsumo(pm, id, fecha, id_usuario, tipo_documento_usuario, id_servicio, id_habitacion);
			tx.commit();

			log.trace ("insercionconsumo: " + id + ": " + tuplasInsertadas + " tuplas insertadas");
			return new Consumo(id, fecha, id_usuario, id_servicio);
		}
		catch (Exception e)
		{
			//        	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
	 * Método que elimina, de manera transaccional, una tupla en la tabla Bebida, dado el identificador de la bebida
	 * Adiciona entradas al log de la aplicación
	 * @param idBebida - El identificador de la bebida
	 * @return El número de tuplas eliminadas. -1 si ocurre alguna Excepción
	 */
	public long eliminarConsumoPorId (long idConsumo) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long resp = sqlConsumo.eliminarConsumoPorId (pm, idConsumo);
            tx.commit();

            return resp;
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
            return -1;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
 
	/**
	 * Método que consulta todas las tuplas en la tabla Bebida
	 * @return La lista de objetos Bebida, construidos con base en las tuplas de la tabla BEBIDA
	 */
	public List<Consumo> darConsumos ()
	{
		return sqlConsumo.darConsumos(pmf.getPersistenceManager());
	}
	
	
	/**
	 * Método que consulta todas las tuplas en la tabla Consuom con un identificador dado
	 * @param idTipoBebida - El identificador del tipo de bebida
	 * @return El objeto TipoBebida, construido con base en las tuplas de la tabla TIPOBEBIDA con el identificador dado
	 */
	public Consumo darConsumoPorId (long idConsumo)
	{
		return sqlConsumo.darConsumoPorId (pmf.getPersistenceManager(), idConsumo);
	}
	
	/* ****************************************************************
	 * 			Métodos para manejar los USUARIOS
	 *****************************************************************/
	/**
	 * M�todo que inserta, de manera transaccional, una tupla en la tabla Bebida
	 * Adiciona entradas al log de la aplicaci�n
	 * @param id 
	 * @param fecha 
	 * @param id_usuario 
	 * @param tipo_documento_usuario 
	 * @param id_servicio 
	 * @param id_habitacion 
	 * @param nombre - El nombre de la bebida
	 * @param idTipoBebida - El identificador del tipo de bebida (Debe existir en la tabla TipoBebida)
	 * @param gradoAlcohol - El grado de alcohol de la bebida (mayor que 0)
	 * @return El objeto Bebida adicionado. null si ocurre alguna Excepci�n
	 */

	public Usuario adicionarUsuario(long num_identidad, String tipo_documento, String nombre, String apellido, String correo, long tipo_usuario, long id_reserva, long id_hotel) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();            
			long tuplasInsertadas = sqlUsuario.adicionarUsuario(pm, num_identidad, tipo_documento, nombre, apellido, correo, tipo_usuario, id_reserva, id_hotel);
			tx.commit();

			return new Usuario(tipo_documento, num_identidad, nombre, apellido, correo);
		}
		catch (Exception e)
		{
			//        	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}

	/**
	 * Método que elimina, de manera transaccional, una tupla en la tabla Bebida, dado el identificador de la bebida
	 * Adiciona entradas al log de la aplicación
	 * @param idBebida - El identificador de la bebida
	 * @return El número de tuplas eliminadas. -1 si ocurre alguna Excepción
	 */
	public long eliminarUsuarioPorId (long id, String tipoDoc) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long resp = sqlUsuario.eliminarUsuarioPorId (pm, id, tipoDoc);
            tx.commit();

            return resp;
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
            return -1;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}

	/**
	 * Método que consulta todas las tuplas en la tabla Bebida
	 * @return La lista de objetos Bebida, construidos con base en las tuplas de la tabla BEBIDA
	 */
	public List<Usuario> darUsuarios ()
	{
		return sqlUsuario.darUsuarios(pmf.getPersistenceManager());
	}


	/**
	 * Método que consulta todas las tuplas en la tabla Consuom con un identificador dado
	 * @param idTipoBebida - El identificador del tipo de bebida
	 * @return El objeto TipoBebida, construido con base en las tuplas de la tabla TIPOBEBIDA con el identificador dado
	 */
	public Usuario darUsuarioPorId (long id, String tipoDoc)
	{
		return sqlUsuario.darUsuarioPorId (pmf.getPersistenceManager(), id, tipoDoc);
	}

	
	/* ****************************************************************
	 * 			M�todos para manejar los RESERVAS
	 *****************************************************************/
	/**
	 * M�todo que inserta, de manera transaccional, una tupla en la tabla Bebida
	 * Adiciona entradas al log de la aplicaci�n
	 * @param id 
	 * @param fecha 
	 * @param id_usuario 
	 * @param tipo_documento_usuario 
	 * @param id_servicio 
	 * @param id_habitacion 
	 * @param nombre - El nombre de la bebida
	 * @param idTipoBebida - El identificador del tipo de bebida (Debe existir en la tabla TipoBebida)
	 * @param gradoAlcohol - El grado de alcohol de la bebida (mayor que 0)
	 * @return El objeto Bebida adicionado. null si ocurre alguna Excepci�n
	 */
	public Reserva adicionarReserva(long id, int numPersonas, Timestamp entrada, Timestamp salida, PlanConsumo pc, Habitacion h) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();            
			long tuplasInsertadas = sqlReserva.adicionarReserva(pm, id, numPersonas, entrada, salida, pc.getId(), h.getId());
			tx.commit();
 
			return new Reserva(id, entrada, salida, numPersonas);
		}
		catch (Exception e)
		{
			//        	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
	 * Método que elimina, de manera transaccional, una tupla en la tabla Bebida, dado el identificador de la bebida
	 * Adiciona entradas al log de la aplicación
	 * @param idBebida - El identificador de la bebida
	 * @return El número de tuplas eliminadas. -1 si ocurre alguna Excepción
	 */
	public long eliminarReservaPorId (long id) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long resp = sqlReserva.eliminarReservaPorId (pm, id);
            tx.commit();

            return resp;
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
            return -1;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
 
	/**
	 * Método que consulta todas las tuplas en la tabla Bebida
	 * @return La lista de objetos Bebida, construidos con base en las tuplas de la tabla BEBIDA
	 */
	public List<Reserva> darReservas()
	{
		return sqlReserva.darReservas(pmf.getPersistenceManager());
	}
	
	
	/**
	 * Método que consulta todas las tuplas en la tabla Consuom con un identificador dado
	 * @param idTipoBebida - El identificador del tipo de bebida
	 * @return El objeto TipoBebida, construido con base en las tuplas de la tabla TIPOBEBIDA con el identificador dado
	 */
	public Reserva darReservaPorId (long id)
	{
		return sqlReserva.darReservaPorId (pmf.getPersistenceManager(), id);
	}
	
	/* ****************************************************************
	 * 			M�todos para manejar los SERVICIOS
	 *****************************************************************/
	
	/**
	 * Método que consulta todas las tuplas en la tabla Bebida
	 * @return La lista de objetos Bebida, construidos con base en las tuplas de la tabla BEBIDA
	 */
	public List<Servicio> darServicios()
	{
		return sqlServicio.darServicios(pmf.getPersistenceManager());
	}
	
	
	/**
	 * Método que consulta todas las tuplas en la tabla Consuom con un identificador dado
	 * @param idTipoBebida - El identificador del tipo de bebida
	 * @return El objeto TipoBebida, construido con base en las tuplas de la tabla TIPOBEBIDA con el identificador dado
	 */
	public Servicio darServicioPorId (long id)
	{
		return sqlServicio.darServicioPorId (pmf.getPersistenceManager(), id);
	}
	
	
	public List<Object []> darReservaPorUsuario(long idUser)
	{
		return sqlReserva.darReservaUsuarios(pmf.getPersistenceManager(), idUser);
	}
	
	
	
	/* ****************************************************************
	 * 			M�todos para manejar los Check
	 *****************************************************************/
	/**
	 * M�todo que inserta, de manera transaccional, una tupla en la tabla Bebida
	 * Adiciona entradas al log de la aplicaci�n
	 * @param id 
	 * @param fecha 
	 * @param id_usuario 
	 * @param tipo_documento_usuario 
	 * @param id_habitacion 
	 * @param entrada 
	 * @return El objeto Check adicionado. null si ocurre alguna Excepci�n
	 */

	public Check adicionarCheck(long id, Timestamp fecha, int entrada, long id_usuario, String tipo_documento_usuario,  long id_habitacion) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();            
			long tuplasInsertadas = sqlCheck.adicionarCheck(pm, id, fecha, entrada, id_usuario, tipo_documento_usuario, id_habitacion);
			tx.commit();

			log.trace ("insercionconsumo: " + id + ": " + tuplasInsertadas + " tuplas insertadas");
			boolean ingreso = false;
			if(entrada==1)
				ingreso= true;
			return new Check(id, fecha, ingreso, id_usuario, tipo_documento_usuario, id_habitacion);
		}
		catch (Exception e)
		{
			//        	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
	 * Método que elimina, de manera transaccional, una tupla en la tabla Bebida, dado el identificador de la bebida
	 * Adiciona entradas al log de la aplicación
	 * @param idBebida - El identificador de la bebida
	 * @return El número de tuplas eliminadas. -1 si ocurre alguna Excepción
	 */
	public long eliminarCheckPorId (long idCheck) 
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long resp = sqlCheck.eliminarCheckPorId (pm, idCheck);
            tx.commit();

            return resp;
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
            return -1;
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
	}
 
	/**
	 * Método que consulta todas las tuplas en la tabla Bebida
	 * @return La lista de objetos Bebida, construidos con base en las tuplas de la tabla BEBIDA
	 */
	public List<Check> darCheck ()
	{
		return sqlCheck.darChecks(pmf.getPersistenceManager());
	}
	
	
	/**
	 * Método que consulta todas las tuplas en la tabla Consuom con un identificador dado
	 * @param idTipoBebida - El identificador del tipo de bebida
	 * @return El objeto TipoBebida, construido con base en las tuplas de la tabla TIPOBEBIDA con el identificador dado
	 */
	public Check darCheckPorId (long idCheck)
	{
		return sqlCheck.darCheckPorId (pmf.getPersistenceManager(), idCheck);
	}

	
	
	
	
	
	
	/**
	 * Elimina todas las tuplas de todas las tablas de la base de datos de HotelAndes
	 * Crea y ejecuta las sentencias SQL para cada tabla de la base de datos - EL ORDEN ES IMPORTANTE 
	 * @return Un arreglo con números que indican el número de tuplas borradas en las tablas 
	 */
	public long [] limpiarHotelAndes ()
	{
		PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            long [] resp = sqlUtil.limpiarHotelAndes (pm);
            tx.commit ();
            log.info ("Borrada la base de datos");
            return resp;
        }
        catch (Exception e)
        {
//        	e.printStackTrace();
        	log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
        	return new long[] {-1, -1, -1, -1, -1, -1, -1};
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
		
	}

	

}