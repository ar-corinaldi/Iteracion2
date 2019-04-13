package hotelAndes.negocio;


public interface VOUsuario {

	public String getTipoDoc();
	
	public Long getNumeroDoc();
	
	public String getNombre();
	
	public String getApellido();
	
	public String getCorreo();
	
	public Reserva getReserva();
}
