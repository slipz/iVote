<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>iVote</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
  	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  	<link rel="stylesheet" href="css/style.css">
  	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  	<script type="application/javascript" src="js/websockets.js"></script>
    <s:head />
    <script type="text/javascript">

    var websocket = null;

    window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
        connect('ws://' + window.location.host + '/WebSocket/ws');
    }

    function connect(host) { // connect to the host websocket
        if ('WebSocket' in window)
            websocket = new WebSocket(host);
        else if ('MozWebSocket' in window)
            websocket = new MozWebSocket(host);
        else {
            writeToHistory('Get a real browser which supports WebSocket.');
            return;
        }

        websocket.onopen    = onOpen; // set the event listeners below
        websocket.onclose   = onClose;
        websocket.onmessage = onMessage;
        websocket.onerror   = onError;
    }

    function onOpen(event) {
    	var init = 'user**'+"${AdminBean.username}"
    	websocket.send(init);
    }
    
    function onClose(event) {
        writeToHistory('WebSocket closed.');;
    }
    
    function onMessage(message) { // print the received message
        writeToHistory(message.data);
    }
    
    function onError(event) {
        writeToHistory('WebSocket error (' + event.data + ').');
    }
    
    function doSend() {
    }

    function writeToHistory(text) {
    	var m0 = text.split("**");
    	if(m0[0] == 'voto'){
    		var msgf = m0[1];
    		console.log(m0[1]);
	    	var m = m0[1].split("\t");
	    	var m1 = m[1].split("->");
	    	var m2 = m1[1].trim();
	    	var f1 = m2.split(" ");
	    	console.log(f1); //idEleicao do voto
	    	var i1 = document.getElementById("idel").innerHTML;
	    	var i2 = i1.split(" ");
	    	if(i2[1] == f1[0]){
	    		var history = document.getElementById('history');
	            var line = document.createElement('p');
	            line.style.wordWrap = 'break-word';
	            line.innerHTML = msgf;
	            history.appendChild(line);
	            history.scrollTop = history.scrollHeight;
	    		
	    	}else{
	    		console.log("o voto nao e pra ti")
	    	}
    	}else{
    		console.log("n e pra mim");
    	}
		
    }
    
    </script>
    
</head>
<body style="background-color: #ececec;">
<!-- top navbar start -->
<nav class="navbar navbar-inverse navbar-fixed-top">
  <div class="container-fluid">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>                        
      </button>
      <a class="navbar-brand active" href="<s:url action="homeAction"/>">iVote</a>
    </div>
    <div class="collapse navbar-collapse" id="myNavbar">
      <ul class="nav navbar-nav">
        
        <li><a href="<s:url action="popularAction"/>">Registar Eleitor</a></li>
        
        <li class="dropdown">
          <a class="dropdown-toggle" data-toggle="dropdown" href="#">Gestao Entidades <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="<s:url action="goAlteraPessoa"/>">Alterar informacoes de um eleitor</a></li>
            <li><a href="<s:url action="goCriaDepartamentoAction"/>">Registar um novo departamento</a></li>
            <li><a href="<s:url action="goCriaFaculdadeAction"/>">Registar uma nova faculdade</a></li>
            <li><a href="<s:url action="goAlteraDepAction"/>">Alterar informacoes de um departamento</a></li>
            <li><a href="<s:url action="goAlteraFacAction"/>">Alterar informacoes de uma faculdade</a></li>
            <li><a href="<s:url action="goImprimeDepartamentos"/>">Imprimir Departamentos</a></li>
            <li><a href="<s:url action="goImprimeFaculdades"/>">Imprimir Faculdades</a></li>
          </ul>
        </li>
        
        <li class="dropdown">
          <a class="dropdown-toggle" data-toggle="dropdown" href="#">Gestao Eleicoes <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="<s:url action="goCriaEleicaoAction"/>">Criar uma nova eleicao</a></li>
            <li><a href="<s:url action="goCriaListaCandidatosAction"/>">Adicionar listas de candidatos a uma eleicao</a></li>
            <li><a href="<s:url action="goAlteraEleicaoAction"/>">Alterar propriedades de uma eleicao</a></li>
            <li><a href="<s:url action="goAdicionaMesaVoto"/>">Adicionar mesas de voto associadas a uma eleicao</a></li>
            <li><a href="<s:url action="goRemoveMesaVoto"/>">Remover mesas de voto associadas a uma eleicao</a></li>
            <li><a href="<s:url action="goImprimeEleicoesAction"/>">Imprimir eleicoes no sistema</a></li>
          </ul>
        </li>
        
        <li class="dropdown">
          <a class="dropdown-toggle" data-toggle="dropdown" href="#">Gestao Consultas <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="<s:url action="goRegistoEleitor"/>">Ver em que local votou um eleitor</a></li>
            <li><a href="<s:url action="#"/>">Ver notificacoes em tempo real</a></li>
          </ul>
        </li>
        
        <li><a href="<s:url action="goMostraMesasOnline"/>">Mostrar Mesas de voto ligadas ao servidor</a></li>
        
      </ul>
      
      <ul class="nav navbar-nav navbar-right">
        <li><a href="<s:url action="goLogout"/>"><span class="glyphicon glyphicon-log-out"></span> Log out</a></li>
      </ul>
    </div>
  </div>
</nav>
<!-- top navbar end -->



    <!-- code for page start -->

    <br>
    <br>
    <br>
    <br>
    
    <div class="container-fluid">
    
        <s:if test="hasActionErrors()">
		  	<div cssClass="alert alert-danger" role="alert">
		  		<s:actionerror/>
			</div>
		</s:if>
		
		<s:if test="hasActionMessages()">
			<div cssClass="alert alert-success" role="alert">
		  		<s:actionmessage/>
			</div>
		</s:if>
		
		  <div class="col-md-4">
		  	  <h3 style="padding-left: 3%;">Detalhes</h3>
		      <div class="card text-center" style="padding: 3%;">
		        <div class="card-block" style="border-style:solid; border-width: thin; border-color: #000000; background-color: #d3d3d3; border-radius: 3%;">
		          <h3 id="idel" class="card-title">idEleicao: <c:out value="${AdminBean.resultMap['idEleicao']}"/></h3>
		          <p class="card-text">Tipo: <c:out value="${AdminBean.resultMap['tipo']}"/></p>
		          <p class="card-text">Titulo: <c:out value="${AdminBean.resultMap['titulo']}"/></p>
		          <p class="card-text">Descricao: <c:out value="${AdminBean.resultMap['descricao']}"/></p>
		          <p class="card-text">Data de Inicio: <c:out value="${AdminBean.resultMap['dataI']}"/></p>
		          <p class="card-text">Data de Fim: <c:out value="${AdminBean.resultMap['dataF']}"/></p>
		        </div>
		      </div>
		      <div id="container"><div id="history"></div></div>
		      
		    </div>
		  
		  <div class="col-md-4">
		  	<h3 style="padding-left: 3%;">Resultados das Listas</h3>
		  	<c:if test="${AdminBean.resultMap['finished'] == 'true'}">
		  		<c:if test="${AdminBean.contagem > 0 }">
					<c:forEach var="linha" items="${AdminBean.resultContagem}">
						<div class="card text-center" style="padding: 3%;">
						
							<c:if test="${AdminBean.maiorNome == linha['nome']}">
								<div class="card-block" style="border-style:solid; border-width: medium; border-color: #000000; background-color: #cdf0d0; border-radius: 3%;">
									<h3 class="card-title">Nome da Lista: <c:out value="${linha['nome']}"/></h3>
									<p class="card-text">Numero de votos: <c:out value="${linha['contagem']}"/></p>
								</div>
							</c:if>
						
							<c:if test="${AdminBean.maiorNome != linha['nome']}">
								<div class="card-block" style="border-style:solid; border-width: thin; border-color: #000000; background-color: #d3d3d3; border-radius: 3%;">
									<h3 class="card-title">Nome da Lista: <c:out value="${linha['nome']}"/></h3>
									<p class="card-text">Numero de votos: <c:out value="${linha['contagem']}"/></p>
								</div>
							</c:if> 
							
						</div>
					</c:forEach>
				</c:if> 
				<c:if test="${AdminBean.contagem == 0 }">
		    		<h4>Nao existem votos nesta eleicao</h4>
				</c:if> 
			</c:if>
			
			<c:if test="${AdminBean.resultMap['finished'] == 'false'}">
                <h4>A eleicao ainda esta a decorrer</h4>
            </c:if> 
            
            <h3 style="padding-left: 3%;">Resultados dos Locais de Voto</h3>
            <c:if test="${AdminBean.sumDet > 0}">
                <c:forEach var="entrada" items="${AdminBean.resultDetalhes}">
                    <div class="card text-center" style="padding: 3%;">
                      <div class="card-block" style="border-style:solid; border-width: thin; border-color: #000000; background-color: #d3d3d3; border-radius: 3%;">
                        <h3 class="card-title">Total de votos: <c:out value="${entrada['total']}"/></h3>
                        <p class="card-text">Local de Voto: <c:out value="${entrada['local']}"/></p>
                        <p class="card-text">Votos de Alunos: <c:out value="${entrada['tAlunos']}"/></p>
                        <p class="card-text">Votos de Funcionarios: <c:out value="${entrada['tFunc']}"/></p>
                        <p class="card-text">Votos de Docentes: <c:out value="${entrada['tDoc']}"/></p>
                      </div>
                    </div>
                  </c:forEach>
            </c:if> 
            
            <c:if test="${AdminBean.sumDet == 0}">
                <h4>Ainda nao existem detalhes de votos para a eleicao</h4>
            </c:if> 
			
			<c:if test="${AdminBean.resultMap['finished'] == 'false'}">
		    	<h4>A eleicao ainda esta a decorrer</h4>
			</c:if> 

		  </div>
		  
		  <div class="col-md-4">
		  	<h3 style="padding-left: 3%;">Listas Candidatas</h3>
		  	<c:if test="${AdminBean.sum > 0}">
		    	<c:forEach var="linha" items="${AdminBean.result}">
			    	<div class="card text-center" style="padding: 3%;">
				      <div class="card-block" style="border-style:solid; border-width: thin; border-color: #000000; background-color: #d3d3d3; border-radius: 3%;">
				        <h3 class="card-title">Nome da Lista: <c:out value="${linha['nome']}"/></h3>
				        <p class="card-text">Tipo da lista: <c:out value="${linha['tipo']}"/></p>
				      </div>
				    </div>
				  </c:forEach>
			</c:if>

		    <c:if test="${AdminBean.sum == 0 }">
		    	<h4>Não existem listas de candidatos para esta eleicao</h4>
			</c:if> 
		  </div>
		
        
	<br>
    <br>
    <br>
    <br>
	
    </div> 

    <!-- code for page end -->



    <!-- bottom navbar start -->
    <nav class="navbar navbar-inverse navbar-fixed-bottom">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="<s:url action="homeAction"/>">iVote</a>
                <p class="navbar-text pull-right">Sistemas Distribuidos 2017/2018</p> 
            </div>
        </div>
    </nav>
    <!-- bottom navbar end -->
    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
</body>
</html>