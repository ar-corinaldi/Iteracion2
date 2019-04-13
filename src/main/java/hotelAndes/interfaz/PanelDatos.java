package hotelAndes.interfaz;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class PanelDatos extends JPanel{
	
	/******************************************************************************
	 * ATRIBUTOS DE INTERFAZ
	 ******************************************************************************/
	/**
	 * �rea de texto con barras de deslizamiento
	 */
	private JTextArea textArea;
	
	/**
     * Construye el panel
     * 
     */

	/******************************************************************************
	 * CONSTRUCTOR
	 ******************************************************************************/
    public PanelDatos ()
    {
        setBorder (new TitledBorder ("Panel de informaci�n"));
        setLayout( new BorderLayout( ) );
        
        textArea = new JTextArea("Aqu� sale el resultado de las operaciones solicitadas");
        textArea.setEditable(false);
        add (new JScrollPane(textArea), BorderLayout.CENTER);
    }
    

	/******************************************************************************
	 * METODOS
	 ******************************************************************************/
    
    /**
     * Actualiza el panel con la informaci�n recibida por par�metro.
     * @param texto El texto con el que actualiza el �rea
     */
    public void actualizarInterfaz (String texto)
    {
    	textArea.setText(texto);
    }
}
