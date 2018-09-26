package ws;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;

import rmiserver.RMI_S_I;


@ServerEndpoint(value = "/ws")
public class WebSocketAnnotation{
	private static final Set<WebSocketAnnotation> users = new CopyOnWriteArraySet<>();
    private static final AtomicInteger sequence = new AtomicInteger(1);
    private String username;
    private WebSocketServer webSocketServer;
    private RMI_S_I rmiserver = null;
    private Session session;

    public WebSocketAnnotation() {
        username = "User" + sequence.getAndIncrement();
    }

    @OnOpen
    public void start(Session session) {
    	System.out.println("nova websocket " + username);
        this.session = session;
        
        try {
            rmiserver = (RMI_S_I) Naming.lookup("rmi://localhost:6501/rmi");
            this.webSocketServer = new WebSocketServer(this);
            
        } catch(NotBoundException | RemoteException | MalformedURLException e){
            System.out.println("RMI is unavailable...");
        }
        
        users.add(this);
    }

    @OnClose
    public void end() {
    	System.out.println("close websocket " + username);
    	// clean up once the WebSocket connection is closed
     	// clean up once the WebSocket connection is closed
        try {
            rmiserver.unsubscribeWebSocket(webSocketServer);
            users.remove(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void receiveMessage(String message) {
		// one should never trust the client, and sensitive HTML
        // characters should be replaced with &lt; &gt; &quot; &amp;
    	if(message.equals("mesasvoto")){
    		try{
    			rmiserver.printMesasVotoWeb();
    		}catch(RemoteException e){
    			e.printStackTrace();
    		}
    	}else if(message.equals("eleitoresonline")){
    		String msg = "online**";
    		for(WebSocketAnnotation x: users){
    			msg = msg + x.username + "\n";
    		}
    		sendMessage(msg);
    	}else{
    		this.username = message;
    		try {
				rmiserver.subscribeWebSocket(webSocketServer);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    		sendOnlineHey();
    	}
    	
    }
    
    @OnError
    public void handleError(Throwable t) {
    }

    private void sendMessage(String text) {
    	// uses *this* object's session to call sendText()
    	try {
			this.session.getBasicRemote().sendText(text);
		} catch (IOException e) {
			// clean up once the WebSocket connection is closed
			try {
				this.session.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
    }
    
    public void notificaAll(String message){
    	for(WebSocketAnnotation x:users){
    		x.sendMessage(message);
    	}
    }
    
    public void notificaOne(String message, String username){
    	Map<String,String> send = new HashMap<>();
    	for(WebSocketAnnotation x:users){
    		if(this.username.equals(username) && !send.containsKey(x.username)){
    			send.put(x.username, "true");
    			x.sendMessage(message);
    		}
    	}
    }
    
    public void sendOnlineHey(){
    	String msg = "online**";
    	for(WebSocketAnnotation x:users){
    		msg = msg + x.username + "\n";
    	}
    	for(WebSocketAnnotation x:users){
    		if(!(x.username.equals(this.username))){
    			x.sendMessage(msg);
    		}
    	}
    	
    }
    
    public String getUsername(){
    	return this.username;
    }

}
