package messageModelTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static protocol.Protocol.CONNECT_RESPONSE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import messageModel.ConnectMessage;
import messageModel.ConnectResp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class ConnectRespTest {
  private ConnectResp right;
  private ConnectResp diff;
  private ConnectResp backup;

  private String message;
  private String diffMessage;

  @BeforeEach
  public void setUp() {
    message = "connected!";
    diffMessage = "not connected";
    right = new ConnectResp(true, message);
    diff = new ConnectResp(false, message);
    backup = new ConnectResp(true, message);
  }

  @Test
  public void testGetSuccess() {
    assertEquals(true, right.getSuccess());
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

    assertEquals(CONNECT_RESPONSE, dataInputStream.readInt());
    assertEquals(true, dataInputStream.readBoolean());
    assertEquals(true, dataInputStream.readUTF().equals(message));
  }

  @Test
  void testReceive() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    dataOutputStream.writeInt(CONNECT_RESPONSE);
    dataOutputStream.writeBoolean(true);
    dataOutputStream.writeUTF(message);
    dataOutputStream.flush();

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    ConnectResp receivedMessage = ConnectResp.receive(dataInputStream);
    assertEquals(true, receivedMessage.getSuccess());
    assertEquals(true, receivedMessage.getMessage().equals(message));
  }

  /**
   * {@inheritDoc}
   */
  @Test
  void testToString() {
    assertTrue(right.toString().contains("ConnectResp{messageType:"));
  }

  /**
   * {@inheritDoc}
   */
  @Test
  void testHashCode() {
    assertEquals(right.hashCode(), right.hashCode());
    assertEquals(backup.hashCode() ,right.hashCode());
    assertNotEquals(right.hashCode(), diff.hashCode());
  }

  /**
   * {@inheritDoc}
   */
  @Test
  void testEqauls() {
    assertEquals(true,right.equals(right));

    assertEquals(true,right.equals(backup));
    assertEquals(true,backup.equals(right));

    ConnectResp zvalue = new ConnectResp(true, message);
    assertEquals(true,right.equals(zvalue));
    assertEquals(true,zvalue.equals(backup));

    assertEquals(false,right.equals(null));
    assertEquals(false,right.equals(diff));
    ConnectResp diffMes = new ConnectResp(false, diffMessage);
    assertEquals(false,right.equals(diffMes));
  }

}
