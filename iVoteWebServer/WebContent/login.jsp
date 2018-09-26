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
  </div>
</nav>
<!-- top navbar end -->


    <!-- sign in start -->
    <br>
    <br>
    <br>
    <br>
    <div class="col-md-4"></div>
   	  <div class="col-md-4">
	    <div class="container col-md-12">
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
	    
	      <s:form theme="bootstrap" cssClass="form-signin" action="login">
	        <h2 class="form-signin-heading">Please log in</h2><br>
	        <label for="inputEmail" class="sr-only">Username</label>
	        <s:textfield theme="simple" type="text" name="username" cssClass="form-control" placeholder="Username" required="true"/><br>
	        <label for="inputPassword" class="sr-only">Password</label>
	        <s:textfield theme="simple" type="password" name="password" cssClass="form-control" placeholder="Password" required="true"/><br><br>
	        <s:submit theme="simple" cssClass="btn btn-outline-secondary btn-block" type="submit" value="Log in"/>
	      </s:form>
	    <br>
	    <br>
	    <br>
	    <br>
	    </div>
	   </div>
    <div class="col-md-4"></div>
    <!-- sign in end -->


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