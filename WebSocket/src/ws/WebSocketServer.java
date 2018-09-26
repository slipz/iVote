package ws;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketServer extends UnicastRemoteObject implements WS_I {
    private WebSocketAnnotation webSocketAnnotation;

    WebSocketServer(WebSocketAnnotation webSocketAnnotation) throws RemoteException{
        super();
        this.webSocketAnnotation = webSocketAnnotation;
    }
    
	@Override
	public void notifica_web(String message) throws RemoteException {
		webSocketAnnotation.notificaAll(message);
	}
	
	@Override
	public void notifica_one(String message, String username) throws RemoteException {
		webSocketAnnotation.notificaOne(message,username);
	}
	
	@Override
	public String getUsername() throws RemoteException{
		return webSocketAnnotation.getUsername();
	}
}
