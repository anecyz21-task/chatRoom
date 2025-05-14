package messageModelTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import messageModel.BroadcastMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class BroadcastMessageTest {
  private BroadcastMessage right;
  private BroadcastMessage backup;
  private BroadcastMessage diff;
  private String message;
  private String username;
  private String diffMessage;

  @BeforeEach
  public void setUp() {
    message = "hello";
    username = "annie";
    diffMessage = "bye";
    right = new BroadcastMessage(username, message);
    backup = new BroadcastMessage(username, message);
    diff = new BroadcastMessage(username, diffMessage);
  }

  @Test
  void testGetSender() {
    assertEquals("annie", right.getSenderUsername());
  }

  @Test
  void testGetMessage() {
    assertEquals("hello", right.getMessage());
  }

  @Test
  void testReceive() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    dataOutputStream.writeUTF(username);
    dataOutputStream.writeUTF(message);
    dataOutputStream.flush();

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    BroadcastMessage receivedMessage = BroadcastMessage.receive(dataInputStream);
    assertEquals(username, receivedMessage.getSenderUsername());
    assertEquals(message, receivedMessage.getMessage());
  }

  @Test
  void testSend() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    right.send(dataOutputStream);

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    assertEquals(Protocol.BROADCAST_MESSAGE, dataInputStream.readInt());
    assertEquals(username, dataInputStream.readUTF());
    assertEquals(message, dataInputStream.readUTF());
  }

  @Test
  void testToString() {
    assertTrue(right.toString().contains("BroadcastMessage{messageType:"));
  }

  @Test
  void testHashCode() {
    assertEquals(right.hashCode(), right.hashCode());
    assertEquals(backup.hashCode() ,right.hashCode());
    assertNotEquals(right.hashCode(), diff.hashCode());
  }

  @Test
  void testEqauls() {
    assertEquals(true,right.equals(right));

    assertEquals(true,right.equals(backup));
    assertEquals(true,backup.equals(right));

    BroadcastMessage zvalue = new BroadcastMessage(username, message);
    assertEquals(true,right.equals(zvalue));
    assertEquals(true,zvalue.equals(backup));

    assertEquals(false,right.equals(null));
    assertEquals(false,right.equals(diff));
    BroadcastMessage diffUsername = new BroadcastMessage("zxy", message);
    assertEquals(false,right.equals(diffUsername));
  }
}
