<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!doctype html>

<html lang="en">
<head>
    <title>iVote</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
  	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
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
    	var init = "${AdminBean.username}"
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

    <!-- registar start -->
	<br>
    <br>
    <br>
    <br>
	
    <div class="container">
    
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

      <s:form class="form-signin" action="registar_utilizador">
        <h2 class="form-signin-heading">Registar Eleitor</h2>
        
        <label for="inputNome" class="sr-only">Nome</label>
        <s:textfield type="text" name="nome" class="form-control" placeholder="Nome"/>

        <label for="inputText" class="sr-only">Numero CC</label>
        <s:textfield type="text" name="username" class="form-control" placeholder="Numero do CC"/>
        
		<s:radio labelposition="inline" label="Tipo" name="tipo" list="tipos" value="defaultTipoValue"/>

		<label for="inputText" class="sr-only">Departamento</label>
        <s:textfield type="text" name="departamento" class="form-control" placeholder="Departamento"/>
        
		<label for="inputText" class="sr-only">Contacto</label>
        <s:textfield type="text" name="ntelef" class="form-control" placeholder="Contacto Telefonico"/>
        
		<label for="inputText" class="sr-only">Morada</label>
        <s:textfield type="text" name="morada" class="form-control" placeholder="Morada"/>

		<label for="inputText" class="sr-only">Data</label>
        <s:textfield type="text" name="validade" cssClass="form-control" placeholder="Validade CC (DD/MM/AA)"/>
        
		
        <label for="inputPassword" class="sr-only">Password</label>
        <s:textfield type="password" id="inputPassword" name="password" cssClass="form-control" placeholder="Password"/>
        
        <s:submit cssClass="btn btn-outline-secondary btn-lg" type="submit" value="Registar"/>
      </s:form>
	
	<br>
    <br>
    <br>
    <br>
    </div> <!-- /container -->
    <!-- registar end -->


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