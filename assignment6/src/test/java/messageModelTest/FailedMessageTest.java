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
import messageModel.FailedMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class FailedMessageTest {
  private FailedMessage right;
  private FailedMessage diff;
  private FailedMessage backup;

  private String errorMessage;
  private String diffMessage;

  @BeforeEach
  public void setUp() {
    errorMessage = "fail message";
    diffMessage = "different message";
    right = new FailedMessage(errorMessage);
    diff = new FailedMessage(diffMessage);
    backup = new FailedMessage(errorMessage);
  }

  @Test
  void testGetErrorMessage() {
    assertEquals(errorMessage, right.getErrorMessage());
  }

  @Test
  void testSend() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    right.send(dataOutputStream);

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    assertEquals(Protocol.FAILED_MESSAGE, dataInputStream.readInt());
    assertEquals(errorMessage, dataInputStream.readUTF());
  }

  @Test
  void testReceive() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    dataOutputStream.writeUTF(errorMessage);
    dataOutputStream.flush();

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    FailedMessage receivedMessage = FailedMessage.receive(dataInputStream);
    assertEquals(errorMessage, receivedMessage.getErrorMessage());
  }

  @Test
  void testToString() {
    assertTrue(right.toString().contains("FailedMessage{messageType:"));
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

    FailedMessage zvalue = new FailedMessage(errorMessage);
    assertEquals(true,right.equals(zvalue));
    assertEquals(true,zvalue.equals(backup));

    assertEquals(false,right.equals(null));
    assertEquals(false,right.equals(diff));
  }
}
