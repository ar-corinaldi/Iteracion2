package hotelAndes.negocio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface VOServicio {
	
	public String getNombre();
	
	public String getDescripcion();
	
	public double getCosto();
	
	public long getId();
	
	public List<Object []> getProductos();
	
	public boolean isCargadoHab();
	
	public Date getHoraReserva();
	
	public boolean isReservado();
	
	@Override
	public String toString();
	
}
