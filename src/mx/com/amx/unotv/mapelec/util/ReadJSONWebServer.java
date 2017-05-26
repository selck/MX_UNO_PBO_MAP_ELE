package mx.com.amx.unotv.mapelec.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import mx.com.amx.unotv.mapelec.dto.ParametrosDTO;

import org.apache.log4j.Logger;

public class ReadJSONWebServer {
	
	private final static Logger logger=Logger.getLogger(ReadJSONWebServer.class);
	
	
	public static String getJSON(){
		URL url;
		StringBuffer HTML=new StringBuffer();
		try {
			// get URL content
			//String conectar=parametrosDTO.getURL_WEBSERVER().replace("$ID_CATEGORIA$", categoria);
			ParametrosDTO parametrosDTO=UtilGeneraJSON.obtenerPropiedades();
			//String conectar="http://dev-unotv.tmx-internacional.net/portal/unotv/utils/json/mapa-elecciones/mapa-elecciones.json";
			String conectar=parametrosDTO.getUrlConnect();
			url = new URL(conectar);
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));
			String inputLine;
			/*String fileName = "C:/pruebas/test.html";
			/File file = new File(fileName);
			
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);*/

			while ((inputLine = br.readLine()) != null) {
				HTML.append(inputLine);
				//bw.write(inputLine);
			}
			//bw.close();
			br.close();
		} catch (MalformedURLException e) {
			logger.error("Error getJSON MalformedURLException: ",e);
		} catch (IOException e) {
			logger.error("Error getJSON IOException: ",e);
		}catch (Exception e) {
			System.out.println("Error readjson");
			e.printStackTrace();
		}
		return HTML.toString();
	}
}
