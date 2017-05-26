<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.*" %>
<portlet-client-model:init>
	<portlet-client-model:require module="ibm.portal.xml.*"/>
	<portlet-client-model:require module="ibm.portal.portlet.*"/>   
</portlet-client-model:init>

<script type="text/javascript" src='<%= request.getContextPath() %>/resources/js/jquery-latest.js'></script>
<script type="text/javascript" src='<%= request.getContextPath() %>/resources/js/bootstrap.min.js'></script>
<link rel="stylesheet" href='<%= request.getContextPath() %>/resources/css/bootstrap.min.css'> 


<c:if test="${not empty listTendenciasRequest}">
<form name="formFin" method="post" action="<portlet:actionURL/>">
<div class="container" id="div_generar_json">
  <h2>Mapa Elecciones</h2>
<table class="table table-hover" style="width: 800px">
	<thead>
		<tr>
			<th style="width: 80px">Estado</th>
			<th style="width: 350px">Tendencia</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach var="t" items="${requestScope.listTendenciasRequest}" varStatus="loop">
   		<tr>
   		<td> 
   			 <input type="text" value="${t.name}" disabled="disabled">
   			 <input type="hidden" id="textTendencia_${t.id_estado}" name="textTendencia_${t.id_estado}" value="${t.name}" />
   		</td>
   		<td> 
   			<select id="selectTendencia_${t.id_estado}" name="selectTendencia_${t.id_estado}">
   			<c:forTokens items="Clinton,Tendencia Clinton,No definido,Tendencia Trump,Trump" delims="," var="name" varStatus="loopT">
   				<c:set var="indice" scope="request" value="${loopT.index + 1}"/>
   				<c:choose>
					 <c:when test="${indice == t.tendencia}">
						<option value="${indice}" selected="selected">${name}</option>       
					 </c:when>
					 <c:otherwise>
					 	<option value="${indice}">${name}</option>
					 </c:otherwise>   				
	    		</c:choose>
			</c:forTokens>
			</select> 
   		</td>
   		</tr>
	</c:forEach>
	</tbody>
</table>
 <button type="submit" class="btn btn-info">Generar JSON</button>
</div>
<input type="hidden" id="hdAccion" name="hdAccion" value="generarJSON" />
</form>
</c:if>
<c:if test="${empty listTendenciasRequest}">
:(
	<c:if test="${not empty respuestaJSONRequest}">
		<div class="container" id="muestraInfo">
			<form name="formFin" method="post" action="<portlet:actionURL/>">			
				<h4>Respuesta de la petici&oacute;n</h4>
				<div id="txtMuestraInfo">
				<c:out value="${respuestaJSONRequest}"></c:out>
				</div>
				<button type="submit" class="btn btn-info">Regresar</button>
				<input type="hidden" id="hdAccion" name="hdAccion" value="terminar" />
			</form>
		</div>
	</c:if>
</c:if>