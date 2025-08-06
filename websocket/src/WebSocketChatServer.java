import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.*;

public class WebSocketChatServer extends WebSocketServer {
    private Map<WebSocket, String> clients = Collections.synchronizedMap(new HashMap<>());
    private Set<String> usernames = Collections.synchronizedSet(new HashSet<>());

    public WebSocketChatServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Wait for username in first message
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (!clients.containsKey(conn)) {
            String username = message.trim();
            if (username.isEmpty() || usernames.contains(username)) {
                conn.send("❌ Invalid or duplicate username.");
                conn.close();
                return;
            }
            clients.put(conn, username);
            usernames.add(username);
            broadcast("🔵 " + username + " has joined the chat.");
            return;
        }

        String username = clients.get(conn);
        if (message.equalsIgnoreCase("/logout")) {
            conn.close();
        } else if (message.equalsIgnoreCase("/online")) {
            StringBuilder sb = new StringBuilder("👥 Online users: ");
            for (String user : usernames) sb.append(user).append(" ");
            conn.send(sb.toString().trim());
        } else if (message.startsWith("@")) {
            int spaceIdx = message.indexOf(' ');
            if (spaceIdx == -1) {
                conn.send("❎ Invalid private message format.");
                return;
            }
            String targetUser = message.substring(1, spaceIdx);
            String privateMsg = message.substring(spaceIdx + 1);
            boolean found = false;
            for (Map.Entry<WebSocket, String> entry : clients.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(targetUser)) {
                    entry.getKey().send("💌 [Private from " + username + "]: " + privateMsg);
                    found = true;
                    break;
                }
            }
            if (!found) conn.send("❎ User '" + targetUser + "' not found.");
        } else {
            broadcast("[" + username + "]: " + message);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String username = clients.remove(conn);
        if (username != null) {
            usernames.remove(username);
            broadcast("🔌 " + username + " has left the chat.");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    private void broadcast(String message) {
        for (WebSocket client : clients.keySet()) {
            client.send(message);
        }
    }

    public static void main(String[] args) {
        new WebSocketChatServer(10000).start();
        System.out.println("✅ WebSocket server started on port 10000");
    }
}
