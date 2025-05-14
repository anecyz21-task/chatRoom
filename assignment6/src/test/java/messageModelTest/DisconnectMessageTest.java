package messageModelTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import messageModel.ConnectMessage;
import messageModel.DisconnectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class DisconnectMessageTest {
  private DisconnectMessage right;
  private DisconnectMessage diff;
  private DisconnectMessage backup;

  private String username = "annie";
  private String diffName = "zxy";

  @BeforeEach
  public void setUp() {
    right = new DisconnectMessage(username);
    diff = new DisconnectMessage(diffName);
    backup = new DisconnectMessage(username);
  }

  @Test
  void testGetUsername() {
    assertEquals(username, right.getUsername());
  }

  @Test
  void testSend() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    right.send(dataOutputStream);

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    assertEquals(Protocol.DISCONNECT_MESSAGE, dataInputStream.readInt());
    assertEquals(username, dataInputStream.readUTF());
  }

  @Test
  void testReceive() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    dataOutputStream.writeUTF(username);
    dataOutputStream.flush();

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    DisconnectMessage receivedMessage = DisconnectMessage.receive(dataInputStream);
    assertEquals(username, receivedMessage.getUsername());
  }

  @Test
  void testToString() {
    assertTrue(right.toString().contains("DisconnectMessage{messageType:"));
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

    DisconnectMessage zvalue = new DisconnectMessage(username);
    assertEquals(true,right.equals(zvalue));
    assertEquals(true,zvalue.equals(backup));

    assertEquals(false,right.equals(null));
    assertEquals(false,right.equals(diff));
  }
}
