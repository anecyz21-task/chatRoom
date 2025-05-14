import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

import messageModel.*;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class ClientTest {

  @Test
  void testEqualsHashCodeToString() throws IOException {
    Client c1 = new Client("localhost", 18888, "userA");
    Client c2 = new Client("localhost", 18888, "userA");
    Client c3 = new Client("localhost", 18888, "userB");
    Client c4 = new Client("otherHost", 18888, "userA");

    assertEquals(c1, c1);
    assertEquals(c1, c2);
    assertNotEquals(c1, c3);
    assertNotEquals(c1, c4);
    assertNotEquals(c1, null);
    assertNotEquals(c1, "string");

    assertEquals(c1.hashCode(), c2.hashCode());
    String c1Str = c1.toString();
    assertTrue(c1Str.contains("userA"));
    assertTrue(c1Str.contains("localhost"));
    assertTrue(c1Str.contains("18888"));
  }

  @Test
  void testQueryUserQuiet() throws IOException {
    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(byteOut);

    out.writeInt(2);
    out.writeUTF("userX");
    out.writeUTF("userY");
    out.flush();

    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
    DataInputStream in = new DataInputStream(byteIn);

    ByteArrayOutputStream clientRequestOut = new ByteArrayOutputStream();
    DataOutputStream clientDataOut = new DataOutputStream(clientRequestOut);

    List<String> users = Client.queryUserQuiet(clientDataOut, in, "testUser");

    DataInputStream clientInCheck = new DataInputStream(new ByteArrayInputStream(clientRequestOut.toByteArray()));
    int sentMsgType = clientInCheck.readInt();
    assertEquals(protocol.Protocol.QUERY_CONNECTED_USERS, sentMsgType);
    String sentUsername = clientInCheck.readUTF();
    assertEquals("testUser", sentUsername);

    assertEquals(2, users.size());
    assertTrue(users.contains("userX"));
    assertTrue(users.contains("userY"));
  }

  @Test
  void testSendMessage() throws IOException {
    ByteArrayOutputStream clientRequestOut = new ByteArrayOutputStream();
    DataOutputStream clientDataOut = new DataOutputStream(clientRequestOut);

    Client.sendMessage("receiver", clientDataOut, "HelloReceiver", "senderUser");

    DataInputStream clientInCheck = new DataInputStream(new ByteArrayInputStream(clientRequestOut.toByteArray()));
    int msgType = clientInCheck.readInt();
    assertEquals(protocol.Protocol.DIRECT_MESSAGE, msgType);
    String sender = clientInCheck.readUTF();
    String receiver = clientInCheck.readUTF();
    String content = clientInCheck.readUTF();

    assertEquals("senderUser", sender);
    assertEquals("receiver", receiver);
    assertEquals("HelloReceiver", content);
  }

  @Test
  void testBroadCast() throws IOException {
    ByteArrayOutputStream clientRequestOut = new ByteArrayOutputStream();
    DataOutputStream clientDataOut = new DataOutputStream(clientRequestOut);

    ByteArrayOutputStream serverResponseOut = new ByteArrayOutputStream();
    DataOutputStream serverDataOut = new DataOutputStream(serverResponseOut);
    new FailedMessage("Broadcast not allowed").send(serverDataOut);
    serverDataOut.flush();

    DataInputStream in = new DataInputStream(new ByteArrayInputStream(serverResponseOut.toByteArray()));

    Client.broadCast(clientDataOut, in, "HelloAll", "senderUser");

    DataInputStream clientInCheck = new DataInputStream(new ByteArrayInputStream(clientRequestOut.toByteArray()));
    int msgType = clientInCheck.readInt();
    assertEquals(protocol.Protocol.BROADCAST_MESSAGE, msgType);
    String sender = clientInCheck.readUTF();
    String content = clientInCheck.readUTF();
    assertEquals("senderUser", sender);
    assertEquals("HelloAll", content);
  }

  @Test
  void testSendInsult() throws IOException {
    ByteArrayOutputStream clientRequestOut = new ByteArrayOutputStream();
    DataOutputStream clientDataOut = new DataOutputStream(clientRequestOut);

    Client.sendInsult("senderUser", "targetUser", clientDataOut);

    DataInputStream clientInCheck = new DataInputStream(new ByteArrayInputStream(clientRequestOut.toByteArray()));
    int msgType = clientInCheck.readInt();
    assertEquals(protocol.Protocol.SEND_INSULT, msgType);
    String sender = clientInCheck.readUTF();
    String recipient = clientInCheck.readUTF();
    assertEquals("senderUser", sender);
    assertEquals("targetUser", recipient);
  }

  @Test
  void testDisconnect() throws IOException {
    ByteArrayOutputStream serverResponseOut = new ByteArrayOutputStream();
    DataOutputStream serverDataOut = new DataOutputStream(serverResponseOut);
    new DisconnectResp(true, "You are no longer connected.").send(serverDataOut);
    serverDataOut.flush();

    DataInputStream in = new DataInputStream(new ByteArrayInputStream(serverResponseOut.toByteArray()));
    ByteArrayOutputStream clientRequestOut = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(clientRequestOut);
  }

  @Test
  void testQueryUser() throws IOException {
    ByteArrayOutputStream serverResponseOut = new ByteArrayOutputStream();
    DataOutputStream serverDataOut = new DataOutputStream(serverResponseOut);
    serverDataOut.writeInt(1);
    serverDataOut.writeUTF("userZ");
    serverDataOut.flush();

    DataInputStream in = new DataInputStream(new ByteArrayInputStream(serverResponseOut.toByteArray()));
    ByteArrayOutputStream clientRequestOut = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(clientRequestOut);

    ByteArrayOutputStream consoleOut = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(consoleOut));

    Client.queryUser(out, in, "testUser");

    System.setOut(originalOut);
    String printed = consoleOut.toString();
    assertTrue(printed.contains("[userZ]"));

    DataInputStream clientInCheck = new DataInputStream(new ByteArrayInputStream(clientRequestOut.toByteArray()));
    int msgType = clientInCheck.readInt();
    assertEquals(protocol.Protocol.QUERY_CONNECTED_USERS, msgType);
    String user = clientInCheck.readUTF();
    assertEquals("testUser", user);
  }

}
