import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

import messageModel.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class ChatServerTest {

  private ChatServer server;
  private Thread serverThread;
  private int assignedPort;

  @BeforeEach
  void setUp() throws IOException {
    ServerSocket testServerSocket = new ServerSocket(0);
    assignedPort = testServerSocket.getLocalPort();
    testServerSocket.close();

    server = new ChatServer(assignedPort);

    serverThread = new Thread(() -> {
      try {
        server.start();
      } catch (IOException ignored) {
      }
    });
    serverThread.start();

    try {
      Thread.sleep(200);
    } catch (InterruptedException ignored) {}
  }

  @AfterEach
  void tearDown() throws IOException, InterruptedException {
    if (server.getServerSocket() != null && !server.getServerSocket().isClosed()) {
      server.getServerSocket().close();
    }
    serverThread.join(500);
  }

  @Test
  void testConnect() throws IOException {
    Socket clientSocket = new Socket("localhost", assignedPort);
    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
    ConnectMessage connectMsg = new ConnectMessage("testUser");
    connectMsg.send(out);

    int msgType = in.readInt();
    assertEquals(protocol.Protocol.CONNECT_RESPONSE, msgType);
    boolean success = in.readBoolean();
    String message = in.readUTF();
    assertTrue(success);
    assertTrue(message.contains("testUser"));
//
//    // 3. Query users when only one user is connected (this user)
//    QueryUsers query = new QueryUsers("testUser");
//    query.send(out);
//
//    // Expect a QueryResp back:
//    msgType = in.readInt();
//    assertEquals(protocol.Protocol.QUERY_USER_RESPONSE, msgType);
//    int numberOfUsers = in.readInt(); // other connected users except "testUser"
//    // Since this is the only connected user, numberOfUsers should be 0
//    assertEquals(0, numberOfUsers);
//
//    // 4. Connect a second user to test querying other connected users
//    Socket clientSocket2 = new Socket("localhost", assignedPort);
//    DataOutputStream out2 = new DataOutputStream(clientSocket2.getOutputStream());
//    DataInputStream in2 = new DataInputStream(clientSocket2.getInputStream());
//    ConnectMessage connectMsg2 = new ConnectMessage("otherUser");
//    connectMsg2.send(out2);
//
//    // Read connect resp for the second user
//    int msgType2 = in2.readInt();
//    assertEquals(protocol.Protocol.CONNECT_RESPONSE, msgType2);
//    boolean success2 = in2.readBoolean();
//    String msg2 = in2.readUTF();
//    assertTrue(success2);
//    assertTrue(msg2.contains("otherUser"));
//
//    // Now query again from first user, expecting "otherUser" in the list
//    query.send(out);
//    msgType = in.readInt();
//    assertEquals(protocol.Protocol.QUERY_USER_RESPONSE, msgType);
//    numberOfUsers = in.readInt();
//    assertEquals(1, numberOfUsers);
//    String fetchedUser = in.readUTF();
//    assertEquals("otherUser", fetchedUser);
//
//    // 5. Disconnect testUser
//    DisconnectMessage disMsg = new DisconnectMessage("testUser");
//    disMsg.send(out);
//    msgType = in.readInt();
//    // The server code uses DisconnectResp message type as OTHER (28) in code snippet
//    // but let's verify:
//    // Actually, DisconnectResp uses Protocol.OTHER (28), we must check that:
//    assertEquals(protocol.Protocol.OTHER, msgType);
//    boolean disconnectSuccess = in.readBoolean();
//    String disconnectMsg = in.readUTF();
//    assertTrue(disconnectSuccess);
//    assertTrue(disconnectMsg.contains("no longer connected"));

    clientSocket.close();
//    clientSocket2.close();
  }

  @Test
  void testQueryOneUser() throws IOException {
    Socket clientSocket = new Socket("localhost", assignedPort);
    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
    ConnectMessage connectMsg = new ConnectMessage("testUser");
    connectMsg.send(out);

    int msgType = in.readInt();
    assertEquals(protocol.Protocol.CONNECT_RESPONSE, msgType);
    boolean success = in.readBoolean();
    String message = in.readUTF();
    assertTrue(success);
    assertTrue(message.contains("testUser"));

    QueryUsers query = new QueryUsers("testUser");
    query.send(out);

    msgType = in.readInt();
    assertEquals(protocol.Protocol.QUERY_USER_RESPONSE, msgType);
    int numberOfUsers = in.readInt();
    assertEquals(0, numberOfUsers);

    clientSocket.close();
  }

  @Test
  void testSecondConnect() throws IOException {
    Socket clientSocket = new Socket("localhost", assignedPort);
    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
    ConnectMessage connectMsg = new ConnectMessage("testUser");
    connectMsg.send(out);

    int msgType = in.readInt();
    assertEquals(protocol.Protocol.CONNECT_RESPONSE, msgType);
    boolean success = in.readBoolean();
    String message = in.readUTF();
    assertTrue(success);
    assertTrue(message.contains("testUser"));

    QueryUsers query = new QueryUsers("testUser");
    query.send(out);

    msgType = in.readInt();
    assertEquals(protocol.Protocol.QUERY_USER_RESPONSE, msgType);
    int numberOfUsers = in.readInt();
    assertEquals(0, numberOfUsers);

    Socket clientSocket2 = new Socket("localhost", assignedPort);
    DataOutputStream out2 = new DataOutputStream(clientSocket2.getOutputStream());
    DataInputStream in2 = new DataInputStream(clientSocket2.getInputStream());
    ConnectMessage connectMsg2 = new ConnectMessage("otherUser");
    connectMsg2.send(out2);

    int msgType2 = in2.readInt();
    assertEquals(protocol.Protocol.CONNECT_RESPONSE, msgType2);
    boolean success2 = in2.readBoolean();
    String msg2 = in2.readUTF();
    assertTrue(success2);
    assertTrue(msg2.contains("otherUser"));
  }

  @Test
  void testQueryTwoUsers() throws IOException {
    Socket clientSocket = new Socket("localhost", assignedPort);
    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
    ConnectMessage connectMsg = new ConnectMessage("testUser");
    connectMsg.send(out);

    int msgType = in.readInt();
    assertEquals(protocol.Protocol.CONNECT_RESPONSE, msgType);
    boolean success = in.readBoolean();
    String message = in.readUTF();
    assertTrue(success);
    assertTrue(message.contains("testUser"));

    QueryUsers query = new QueryUsers("testUser");
    query.send(out);

    msgType = in.readInt();
    assertEquals(protocol.Protocol.QUERY_USER_RESPONSE, msgType);
    int numberOfUsers = in.readInt();
    assertEquals(0, numberOfUsers);

    Socket clientSocket2 = new Socket("localhost", assignedPort);
    DataOutputStream out2 = new DataOutputStream(clientSocket2.getOutputStream());
    DataInputStream in2 = new DataInputStream(clientSocket2.getInputStream());
    ConnectMessage connectMsg2 = new ConnectMessage("otherUser");
    connectMsg2.send(out2);

    int msgType2 = in2.readInt();
    assertEquals(protocol.Protocol.CONNECT_RESPONSE, msgType2);
    boolean success2 = in2.readBoolean();
    String msg2 = in2.readUTF();
    assertTrue(success2);
    assertTrue(msg2.contains("otherUser"));

    query.send(out);
    msgType = in.readInt();
    assertEquals(protocol.Protocol.QUERY_USER_RESPONSE, msgType);
    numberOfUsers = in.readInt();
    assertEquals(1, numberOfUsers);
    String fetchedUser = in.readUTF();
    assertEquals("otherUser", fetchedUser);
  }

  @Test
  void testBroadcastDirectAndInsult() throws IOException, InterruptedException {
    ClientConnection userA = new ClientConnection("localhost", assignedPort, "userA");
    ClientConnection userB = new ClientConnection("localhost", assignedPort, "userB");
    ClientConnection userC = new ClientConnection("localhost", assignedPort, "userC");

    userA.connect();
    userB.connect();
    userC.connect();

    assertTrue(userA.isConnected());
    assertTrue(userB.isConnected());
    assertTrue(userC.isConnected());

    BroadcastMessage broadcast = new BroadcastMessage("userA", "Hello all!");
    broadcast.send(userA.out);

    Message msgA = Message.receive(userA.in);
    assertTrue(msgA instanceof FailedMessage, "Sender should receive a FailedMessage after broadcast.");
    Message msgB = Message.receive(userB.in);
    assertTrue(msgB instanceof BroadcastMessage);
    BroadcastMessage bmB = (BroadcastMessage) msgB;
    assertEquals("Hello all!", bmB.getMessage());
    assertEquals("userA", bmB.getSenderUsername());
    Message msgC = Message.receive(userC.in);
    assertTrue(msgC instanceof BroadcastMessage);
    BroadcastMessage bmC = (BroadcastMessage) msgC;
    assertEquals("Hello all!", bmC.getMessage());
    assertEquals("userA", bmC.getSenderUsername());

    DirectMessage dm = new DirectMessage("userB", "userC", "Hi userC, this is a direct message!");
    dm.send(userB.out);

    Message msgC2 = Message.receive(userC.in);
    assertTrue(msgC2 instanceof DirectMessage);
    DirectMessage dmC = (DirectMessage) msgC2;
    assertEquals("userB", dmC.getSenderUsername());
    assertEquals("userC", dmC.getRecipientUsername());
    assertEquals("Hi userC, this is a direct message!", dmC.getMessage());

    InsultMessage insult = new InsultMessage("userC", "userA");
    insult.send(userC.out);

    Message msgC3 = Message.receive(userC.in);
    assertTrue(msgC3 instanceof FailedMessage, "Insult sender should receive a FailedMessage after broadcast.");

    Message msgA2 = Message.receive(userA.in);
    assertTrue(msgA2 instanceof BroadcastMessage);
    BroadcastMessage insultMsgA = (BroadcastMessage) msgA2;
    assertTrue(insultMsgA.getMessage().contains("userC -> userA"));

    Message msgB2 = Message.receive(userB.in);
    assertTrue(msgB2 instanceof BroadcastMessage);
    BroadcastMessage insultMsgB = (BroadcastMessage) msgB2;
    assertTrue(insultMsgB.getMessage().contains("userC -> userA"));

    disconnectUser(userA, "userA");
    disconnectUser(userB, "userB");
    disconnectUser(userC, "userC");

    userA.close();
    userB.close();
    userC.close();
  }

  private void disconnectUser(ClientConnection client, String username) throws IOException {
    DisconnectMessage disMsg = new DisconnectMessage(username);
    disMsg.send(client.out);
    int msgType = client.in.readInt();
    assertEquals(Protocol.OTHER, msgType);
    boolean disSuccess = client.in.readBoolean();
    String disMsgStr = client.in.readUTF();
    assertTrue(disSuccess);
    assertTrue(disMsgStr.contains("no longer connected"));
  }

  static class ClientConnection {
    String host;
    int port;
    String username;
    Socket socket;
    DataOutputStream out;
    DataInputStream in;

    ClientConnection(String host, int port, String username) {
      this.host = host;
      this.port = port;
      this.username = username;
    }

    void connect() throws IOException {
      socket = new Socket(host, port);
      out = new DataOutputStream(socket.getOutputStream());
      in = new DataInputStream(socket.getInputStream());

      // Send connect message
      ConnectMessage cm = new ConnectMessage(username);
      cm.send(out);

      int msgType = in.readInt();
      assertEquals(Protocol.CONNECT_RESPONSE, msgType);
      boolean success = in.readBoolean();
      String message = in.readUTF();
      assertTrue(success, "Connection should be successful for " + username);
      assertTrue(message.contains(username));
    }

    boolean isConnected() {
      return socket != null && socket.isConnected() && !socket.isClosed();
    }

    void close() throws IOException {
      if (socket != null) {
        socket.close();
      }
    }
  }

  @Test
  void testDisconnect() throws IOException {
    Socket clientSocket = new Socket("localhost", assignedPort);
    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
    ConnectMessage connectMsg = new ConnectMessage("testUser");
    connectMsg.send(out);

    int msgType = in.readInt();
    assertEquals(protocol.Protocol.CONNECT_RESPONSE, msgType);
    boolean success = in.readBoolean();
    String message = in.readUTF();
    assertTrue(success);
    assertTrue(message.contains("testUser"));

    QueryUsers query = new QueryUsers("testUser");
    query.send(out);

    msgType = in.readInt();
    assertEquals(protocol.Protocol.QUERY_USER_RESPONSE, msgType);
    int numberOfUsers = in.readInt();
    assertEquals(0, numberOfUsers);

    DisconnectMessage disMsg = new DisconnectMessage("testUser");
    disMsg.send(out);
    msgType = in.readInt();
    assertEquals(protocol.Protocol.OTHER, msgType);
    boolean disconnectSuccess = in.readBoolean();
    String disconnectMsg = in.readUTF();
    assertTrue(disconnectSuccess);
    assertTrue(disconnectMsg.contains("no longer connected"));

    clientSocket.close();
  }

  @Test
  void testEqualsAndHashCode() throws IOException {
    ChatServer sameField = new ChatServer(0);
    assertEquals(sameField, sameField);
    assertEquals(sameField.hashCode(), sameField.hashCode());
    assertEquals(sameField, new ChatServer(0));
    assertEquals(sameField.hashCode(), new ChatServer(0).hashCode());
    assertNotEquals(sameField, new ChatServer(1));
    assertNotEquals(sameField, "");
  }

  @Test
  void testToString() throws IOException {
    assertEquals(new ChatServer(0).toString(), new ChatServer(0).toString());
  }

  @Test
  void testGetter() throws IOException {
    assertEquals(new ChatServer(0).getPort(), new ChatServer(0).getPort());
    assertNotNull(new ChatServer(0).getThreadPool());
    assertNotNull(new ChatServer(0).getInsultGenerator());
  }
}
