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
    	var init = "${UserBean.username}"
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
        
        <li><a href="<s:url action="goListaEleicoesEleitor"/>">Listar eleicoes</a></li>
        
        <li><a href="<s:url action="goRegistoProprio"/>">Consultar local de voto</a></li>
        
        <li class="dropdown">
          <a class="dropdown-toggle" data-toggle="dropdown" href="#">Menu Facebook <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="<s:url action="#"/>">Associar conta existente ao Facebook</a></li>
            <li><a href="<s:url action="#"/>">Partilhar a pagina de uma eleicao no mural</a></li>
            <li><a href="<s:url action="#"/>">Desassociar a conta do Facebook</a></li>
          </ul>
        </li>
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

	    <div class="container">
	    
	  	<h2>Bem-vindo ${session.username}.</h3>
	  	<br>
	  	<h4>Para navegar pelas diversas funcionalidades utilize a barra de navegacao.</h4>

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