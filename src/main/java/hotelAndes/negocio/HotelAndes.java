package hotelAndes.negocio;

import hotelAndes.persistencia.PersistenciaHotelAndes;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

public class HotelAndes {

	/******************************************************************************
	 * CONSTANTES
	 ******************************************************************************/

	private static Logger log = Logger.getLogger(HotelAndes.class.getName());

	/******************************************************************************
	 * ATRIBUTOS
	 ******************************************************************************/
	/**
	 * El manejador de persistencia
	 */
	private PersistenciaHotelAndes pha;

	private int capacidad;

	private String nombre;

	private String pais;

	private String ciudad;

	private List<Usuario> usuarios;

	private List<Habitacion> habitaciones;

	private List<PlanConsumo> planesConsumo;

	private List<Servicio> servicios;

	/******************************************************************************
	 * CONSTRUCTOR
	 ******************************************************************************/

	public HotelAndes() {
		capacidad = 0;
		nombre = "";
		pais = "";
		ciudad = "";
		usuarios = pha.darUsuarios();
		habitaciones = pha.darHabitaciones();
		planesConsumo = new ArrayList<PlanConsumo>();
		servicios = pha.darServicios();
		pha = PersistenciaHotelAndes.getInstance();
	}

	public HotelAndes( int capacidad, String nombre, String pais, String ciudad, List<Usuario> usuarios, 
			List<Habitacion> habitaciones, List<PlanConsumo> planesConsumo, List<Servicio> servicios) {
		this.capacidad = capacidad;
		this.nombre = nombre;
		this.ciudad = ciudad;
		this.pais = pais;
		this.usuarios = usuarios;
		this.habitaciones = habitaciones;
		this.planesConsumo = planesConsumo;
		this.servicios = servicios;

	}

	public HotelAndes(JsonObject tableConfig)
	{
		pha = PersistenciaHotelAndes.getInstance (tableConfig);
	}

	/**
	 * Cierra la conexi�n con la base de datos (Unidad de persistencia)
	 */
	public void cerrarUnidadPersistencia ()
	{
		pha.cerrarUnidadPersistencia ();
	}

	/******************************************************************************
	 * METODOS
	 ******************************************************************************/

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Habitacion> getHabitaciones() {
		return habitaciones;
	}

	public void setHabitaciones(List<Habitacion> habitaciones) {
		this.habitaciones = habitaciones;
	}

	public List<PlanConsumo> getPlanesConsumo() {
		return planesConsumo;
	}

	public void setPlanesConsumo(List<PlanConsumo> planesConsumo) {
		this.planesConsumo = planesConsumo;
	}

	public List<Servicio> getServicios() {
		return servicios;
	}

	public void setServicios(List<Servicio> servicios) {
		this.servicios = servicios;
	}

	@Override
	public String toString() {
		return "HotelAndes [capacidad=" + capacidad + ", nombre=" + nombre
				+ ", pais=" + pais + ", ciudad=" + ciudad + ", usuarios="
				+ usuarios + ", habitaciones=" + habitaciones
				+ ", planesConsumo=" + planesConsumo + ", servicios="
				+ servicios + "]";
	}

	public Consumo adicionarConsumo(long id, Timestamp fecha, long id_usuario, String tipo_documento_usuario, long id_servicio, long id_habitacion) throws Exception
	{
		Consumo con = pha.adicionarConsumo(id, fecha, id_usuario, tipo_documento_usuario, id_servicio, id_habitacion);
		Usuario user = pha.darUsuarioPorId(id_habitacion,tipo_documento_usuario);
		Servicio ser = pha.darServicioPorId(id_servicio);
		if( user != null && ser.isReservado() ){
			user.getConsumos().add(con);
		}
		else{
			throw new Exception( "O el usuario no existe, o el id del consumo es nulo, o ya estaba reservado el servicio." );
			//excepcion 
		}
		
		return con;
	}
	
	public Usuario darUsuario(long id, String tipoDoc){
		return pha.darUsuarioPorId(id, tipoDoc);
	}

	/**
	 * Adiciona de manera persistente un tipo de bebida 
	 * Adiciona entradas al log de la aplicaci�n
	 * @param nombre - El nombre del tipo de bebida
	 * @return El objeto TipoBebida adicionado. null si ocurre alguna Excepci�n
	 */
	public Usuario adicionarUsuario(long num_identidad, String tipo_documento, String nombre, String apellido, String correo, long tipo_usuario, long id_reserva, long id_hotel)
	{
		Usuario usuario = pha.adicionarUsuario(num_identidad, tipo_documento, nombre, apellido, correo, tipo_usuario, id_reserva, id_hotel);		
		//        pha.adicionarReserva(id_reserva, 1, new Timestamp(0), new Timestamp(0), pc, h);
		return usuario;
	}

	public Reserva adicionarReserva(long id, int numPersonas, Timestamp entrada, Timestamp salida, PlanConsumo pc, Habitacion h){
		Reserva reserva = pha.adicionarReserva(id, numPersonas, entrada, salida, pc, h);		
		return reserva;
	}

	public Habitacion adicionarHabitacion(long id, double cuenta_habitacion, long tipo_habitacion, long id_hotel){
		Habitacion h = pha.adicionarHabitacion(id, cuenta_habitacion, tipo_habitacion, id_hotel);		
		return h;
	}

	public TipoHabitacion adicionarTipoHabitacion(long id, String nombre, double costo, int capacidad){
		return pha.adicionarTipoHabitacion(id, nombre, costo, capacidad);		
	}
	
	public Check adicionarCheck(long idCliente, String tipoDocumentoCliente,long idRecepcion, String tipoDocumentoRecepcion, boolean entrada) throws Exception
	{
		Check retornable = null;
		Usuario recepcionista = pha.darUsuarioPorId(idRecepcion, tipoDocumentoRecepcion);
		Usuario cliente = pha.darUsuarioPorId(idCliente, tipoDocumentoCliente);
		Timestamp fechaActual = Timestamp.valueOf(LocalDateTime.now());
		Reserva reserva = cliente.getReserva();
	

		if(pha.darReservaPorId(reserva.getId())==null)
				throw new Exception("No se encuentra reserva del cliente"); 
		if(!(fechaActual.after(reserva.getEntrada())&& fechaActual.before(reserva.getSalida())))
				throw new Exception("la reserva Termino");
		
		retornable =recepcionista.crearCheck(idCliente, tipoDocumentoCliente, fechaActual, entrada, reserva.getHabitacion().getId());
		int ingreso =1;
		if(!retornable.isIngreso())
			ingreso = 0;
		pha.adicionarCheck(retornable.getId(), retornable.getFecha(), ingreso, retornable.getIdUsuario(), retornable.getTipoDocumentoUsuario(), retornable.getIdHabitacion());
		
		return retornable;
		
	}

	public TipoHabitacion darTipoHabitacion(long tipoHabitacion) {
		
		return pha.darTipoHabitacion(tipoHabitacion);
	}

	public List<Consumo> darConsumos() {

		return pha.darConsumos();
	}

	public Servicio darServicio(long idServicio) {
		return pha.darServicioPorId(idServicio);
	}

	
	

}
