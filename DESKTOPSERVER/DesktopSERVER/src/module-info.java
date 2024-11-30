module DesktopSERVER {
	requires java.sql;
	requires json.simple;
	requires Java.WebSocket;
	requires jakarta.persistence;  // anh xa entity table cua database
	requires jdk.httpserver;
	requires spring.web;
	requires java.rmi;
	requires java.base;
	
	requires com.fasterxml.jackson.core;
	requires com.google.api.client.auth;
	requires google.api.client;
	requires com.google.api.client;
	requires com.google.api.client.json.gson;
	requires com.google.api.services.gmail;
	requires com.google.api.client.extensions.jetty.auth;
	requires com.google.api.client.extensions.java6.auth;
	requires org.apache.tomcat.embed.websocket;
	requires mail;
	
	exports Common;
}