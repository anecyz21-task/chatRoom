package messageModelTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import messageModel.BroadcastMessage;
import messageModel.ConnectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class ConnectMessageTest {
  private ConnectMessage right;
  private ConnectMessage diff;
  private ConnectMessage backup;
  private String username;
  private String diffName;

  @BeforeEach
  public void setUp() {
    username = "annie";
    diffName = "zxy";
    right = new ConnectMessage(username);
    diff = new ConnectMessage(diffName);
    backup = new ConnectMessage(username);
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

    assertEquals(Protocol.CONNECT_MESSAGE, dataInputStream.readInt());
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

    ConnectMessage receivedMessage = ConnectMessage.receive(dataInputStream);
    assertEquals(username, receivedMessage.getUsername());
  }

  @Test
  void testToString() {
    assertTrue(right.toString().contains("ConnectMessage{messageType:"));
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

    ConnectMessage zvalue = new ConnectMessage(username);
    assertEquals(true,right.equals(zvalue));
    assertEquals(true,zvalue.equals(backup));

    assertEquals(false,right.equals(null));
    assertEquals(false,right.equals(diff));
  }
}
