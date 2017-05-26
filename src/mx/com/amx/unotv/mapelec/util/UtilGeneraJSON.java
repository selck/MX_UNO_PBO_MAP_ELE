package mx.com.amx.unotv.mapelec.util;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.log4j.Logger;

import mx.com.amx.unotv.mapelec.dto.ParametrosDTO;

public class UtilGeneraJSON {
	
	private final static Logger logger = Logger.getLogger(UtilGeneraJSON.class);
	
	public static ParametrosDTO obtenerPropiedades() {
		ParametrosDTO parametrosDTO = new ParametrosDTO();		 
		try {	    		
			Properties props = new Properties();
		    //props.load(this.getClass().getResourceAsStream( "/general.properties" ));				
			props.load(UtilGeneraJSON.class.getResourceAsStream( "/general.properties" ));
		    parametrosDTO.setPathFiles(props.getProperty("pathFiles"));
		    parametrosDTO.setPathShell(props.getProperty("pathShell"));
			parametrosDTO.setPathShellElimina(props.getProperty("pathShellElimina"));
			parametrosDTO.setPathRemote(props.getProperty("pathRemote"));
			parametrosDTO.setAmbiente(props.getProperty("ambiente"));
			parametrosDTO.setNameJSON(props.getProperty("nameJSON"));
			parametrosDTO.setUrlConnect(props.getProperty("urlConnect"));
		} catch (Exception ex) {
			parametrosDTO = new ParametrosDTO();
			logger.error("No se encontro el Archivo de propiedades: ", ex);			
		}
		return parametrosDTO;
    }
	
	public static boolean createFolders(String carpetaContenido) {
		boolean success = false;
		try {						
			File carpetas = new File(carpetaContenido) ;
			if(!carpetas.exists()) {   
				success = carpetas.mkdirs();					
			} else 
				success = true;							
		} catch (Exception e) {
			success = false;
			logger.error("Ocurrio error al crear las carpetas: ", e);
		} 
		return success;
	}
	public static boolean writeJSON(String rutaJSON, String JSON) {
		boolean success = false;
		try {
			FileWriter fichero = null;
	        PrintWriter pw = null;
	        try {
				fichero = new FileWriter(rutaJSON);				
				pw = new PrintWriter(fichero);							
				pw.println(JSON);
				pw.close();
				success = true;
			} catch(Exception e){			
				logger.error("Error al acceder a la ruta " + rutaJSON + ": ", e);
				success = false;
			}finally{
				try{                    			              
					if(null!= fichero)
						fichero.close();
				}catch (Exception e2){
					success = false;
					logger.error("Error al cerrar el file: ", e2);
				}
			}	
		} catch(Exception e) {
			success = false;
			logger.error("Fallo al crear el JSON: ", e);
		}		
		return success;
	}
	public static boolean transfiereWebServer(ParametrosDTO parametros) {
		boolean success = false;	
		
		String local = parametros.getPathFiles() + "*";
		String remote = parametros.getPathRemote();
		String comando = parametros.getPathShell() + " " + local + " " + remote;
		//String comandoElimina = parametros.getPathShellElimina() + " " + parametros.getPathFiles() + "/*";
		
		logger.debug("comado: "+comando);
		//logger.debug("comadoElimina: "+comandoElimina);
		
		try {								
			Runtime r = Runtime.getRuntime();
			r.exec(comando).waitFor();
			//r.exec(comandoElimina).waitFor();			
			success = true;
		} catch(Exception e) {
			success = false;
			logger.error("Ocurrio un error al ejecutar el Shell " + comando + ": ", e);
		}		
		return success;
	}
}
