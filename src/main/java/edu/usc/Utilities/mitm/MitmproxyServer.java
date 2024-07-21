package edu.usc.Utilities.mitm;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
public class MitmproxyServer extends WebSocketServer {

  public MitmproxyServer(InetSocketAddress address) {
    super(address);
  }

  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    System.out.println("MitmproxyServer: new connection to websocket server" + conn.getRemoteSocketAddress());
  }

  @Override
  public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    System.out.println("MitmproxyServer: closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
  }

  @Override
  public void onMessage(WebSocket conn, String message) {
    System.out.println("MitmproxyServer: received message from " + conn.getRemoteSocketAddress() + ": " + message);
  }

  @Override
  public void onError(WebSocket conn, Exception ex) {
    System.out.println("MitmproxyServer: an error occured on connection ");
  }

}