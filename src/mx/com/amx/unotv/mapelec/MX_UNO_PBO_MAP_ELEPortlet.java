package mx.com.amx.unotv.mapelec;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.portlet.*;

import org.apache.log4j.Logger;

import mx.com.amx.unotv.mapelec.dto.ParametrosDTO;
import mx.com.amx.unotv.mapelec.dto.TendenciaDTO;
import mx.com.amx.unotv.mapelec.util.ReadJSONWebServer;
import mx.com.amx.unotv.mapelec.util.UtilGeneraJSON;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MX_UNO_PBO_MAP_ELEPortlet extends javax.portlet.GenericPortlet {
	
	private final static Logger logger=Logger.getLogger(MX_UNO_PBO_MAP_ELEPortlet.class);
	private final int NUMERO_ESTADOS=50;
	public void init() throws PortletException{
		super.init();
	}

	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		logger.info("===== doView =====");
		String dispatch=(String) (request.getPortletSession().getAttribute("dispatch")==null || request.getPortletSession().getAttribute("dispatch").equals("")?"inicio":request.getPortletSession().getAttribute("dispatch"));
		String respuestaJSONRequest=(String) (request.getPortletSession().getAttribute("respuestaJSONRequest")==null || request.getPortletSession().getAttribute("respuestaJSONRequest").equals("")?"":request.getPortletSession().getAttribute("respuestaJSONRequest"));
		
		Gson gson = new Gson();
		List<TendenciaDTO> listTendencias=new ArrayList<TendenciaDTO>();
		try {
			logger.debug("dispatch: "+dispatch);
			
			if(dispatch.equalsIgnoreCase("inicio")){
				request.getPortletSession().removeAttribute("dispatch");
				dispatch="/resources/jsp/iniMapaElecciones.jsp";
				
				String jsonTXT=ReadJSONWebServer.getJSON();
				
				if(!jsonTXT.equals("")){
					JsonParser parser = new JsonParser();
					JsonObject jsonObjetoPaises = parser.parse(jsonTXT).getAsJsonObject();
					JsonArray jsonArray=jsonObjetoPaises.getAsJsonArray("states");
					/*Generando DTO´S*/
					 for (int i = 0; i < jsonArray.size(); i++) {
				        JsonElement str = jsonArray.get(i);
				        TendenciaDTO obj = gson.fromJson(str, TendenciaDTO.class);
				        listTendencias.add(obj);
					 }
				}
				request.setAttribute("listTendenciasRequest", listTendencias);
			}else if(dispatch.equalsIgnoreCase("GENERO_JSON")){
				request.setAttribute("respuestaJSONRequest", respuestaJSONRequest);
				dispatch="/resources/jsp/iniMapaElecciones.jsp";
			}
			
			response.setContentType(request.getResponseContentType());
			//logger.info("Redirigiendo a: "+dispatch);
			PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher(dispatch);
			rd.include(request,response);
				
		} catch (Exception e) {
			logger.error("Error DoView: ",e);
		}

	}

	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, java.io.IOException {
		logger.info("===== processAction =====");
		boolean success=false;
		try {
			String accion=request.getParameter("hdAccion")==null?"":request.getParameter("hdAccion");
			logger.info("Accion processAction:"+accion);
			
			if(accion.equalsIgnoreCase("terminar")){
				request.getPortletSession().removeAttribute("respuestaJSONRequest");
				request.getPortletSession().setAttribute("dispatch","inicio");
			}else if(accion.equalsIgnoreCase("generarJSON")){
				/*
				Enumeration<String> parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					 String paramName = parameterNames.nextElement();
					 logger.info("paramName: "+paramName);
					 String[] paramValues = request.getParameterValues(paramName);
					 for (int i = 0; i < paramValues.length; i++) {
						 String paramValue = paramValues[i];
						 logger.info("paramValue: "+paramValue);
					 }	 
				}*/
				ArrayList<TendenciaDTO> listTendencias=new ArrayList<TendenciaDTO>();
				String salida="";
				logger.debug("Vamos a generar el JSON");
				for (int i = 1; i <= NUMERO_ESTADOS; i++) {
					TendenciaDTO t=new TendenciaDTO();
					t.setName(request.getParameter("textTendencia_"+i));
					t.setTendencia(Integer.parseInt(request.getParameter("selectTendencia_"+i)));
					t.setId_estado(i);
					listTendencias.add(t);
				}
				ParametrosDTO parametrosDTO = UtilGeneraJSON.obtenerPropiedades();
				String carpeta=parametrosDTO.getPathFiles();
				
				success=UtilGeneraJSON.createFolders(carpeta);
				logger.debug("Creando carpeta:"+carpeta+" "+success);
				carpeta=carpeta.replace("web01", "web02");
				success=UtilGeneraJSON.createFolders(carpeta);
				logger.debug("Creando carpeta:"+carpeta+" "+success);
				if(success){
					logger.debug("Generando JSON...");
					JsonObject jsonEstados=new JsonObject();
					JsonArray jsonArrayTendencias=new JsonArray();
				    for (TendenciaDTO t : listTendencias) {
				    	JsonObject tendenciaJSON=new JsonObject();
				    	tendenciaJSON.addProperty("id_estado", t.getId_estado());
				    	tendenciaJSON.addProperty("name", t.getName());
				    	tendenciaJSON.addProperty("tendencia", t.getTendencia());
				    	jsonArrayTendencias.add(tendenciaJSON);
					}
				    
				    jsonEstados.add("states", jsonArrayTendencias);
				    logger.debug("Escribiendo JSON... ");
				    success=UtilGeneraJSON.writeJSON(parametrosDTO.getPathFiles()+parametrosDTO.getNameJSON(), jsonEstados.toString());
				    logger.debug(success);
				    success=UtilGeneraJSON.writeJSON(parametrosDTO.getPathFiles().replace("web01", "web02")+parametrosDTO.getNameJSON(), jsonEstados.toString());
				    logger.debug(success);
				    if(success){
				    	logger.info("Se creo el json en "+parametrosDTO.getPathFiles()+parametrosDTO.getNameJSON()+": "+success);
				    	if(parametrosDTO.getAmbiente().equalsIgnoreCase("desarrollo")){
				    		salida="Favor de validar json en http://dev-unotv.tmx-internacional.net/portal/unotv/componentes_estaticos/json/especiales/mapa-elecciones.json";
				    	}else{
				    		salida="Los cambios se han guardado exitosamente, el mapa se actualizar&aacute; aproximadamente en 10 minutos.";
				    	}
				    	request.getPortletSession().setAttribute("respuestaJSONRequest", salida);
				    	request.getPortletSession().setAttribute("dispatch", "GENERO_JSON");
				    	
				    	if(parametrosDTO.getAmbiente().equals("desarrollo"))
				    		UtilGeneraJSON.transfiereWebServer(parametrosDTO);
					    }
				}
			}
		} catch (Exception e) {
			logger.error("Error processAction: ",e);
		}
	}

}
