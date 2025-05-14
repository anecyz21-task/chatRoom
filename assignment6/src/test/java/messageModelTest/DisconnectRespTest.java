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
import messageModel.DisconnectResp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class DisconnectRespTest {
  private DisconnectResp right;
  private DisconnectResp diff;
  private DisconnectResp backup;

  private String message;

  @BeforeEach
  public void setUp() {
    message = "disconnect";
    right = new DisconnectResp(true, message);
    diff = new DisconnectResp(false, message);
    backup = new DisconnectResp(true, message);
  }

  @Test
  void testIsSuccess() {
    assertEquals(true, right.isSuccess());
  }

  @Test
  void testGetMessage() {
    assertEquals(message, right.getMessage());
  }

  @Test
  void testSend() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    right.send(dataOutputStream);

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    assertEquals(Protocol.OTHER, dataInputStream.readInt());
    assertEquals(true, dataInputStream.readBoolean());
    assertEquals(message, dataInputStream.readUTF());
  }


  @Test
  void testReceive() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    dataOutputStream.writeBoolean(true);
    dataOutputStream.writeUTF(message);
    dataOutputStream.flush();

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    DisconnectResp receivedMessage = DisconnectResp.receive(dataInputStream);
    assertEquals(true, receivedMessage.isSuccess());
    assertEquals(message, receivedMessage.getMessage());
  }

  @Test
  void testToString() {
    assertTrue(right.toString().contains("DisconnectResp{messageType:"));
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

    DisconnectResp zvalue = new DisconnectResp(true, message);
    assertEquals(true,right.equals(zvalue));
    assertEquals(true,zvalue.equals(backup));

    assertEquals(false,right.equals(null));
    assertEquals(false,right.equals(diff));
    DisconnectResp diffMessage = new DisconnectResp(true, "still here");
    assertEquals(false,right.equals(diffMessage));
  }
}
