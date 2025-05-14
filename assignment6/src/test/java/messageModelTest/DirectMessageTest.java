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
import messageModel.DirectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class DirectMessageTest {
  private DirectMessage right;
  private DirectMessage diff;
  private DirectMessage backup;

  private String sender;
  private String receiverName;
  private String message;

  @BeforeEach
  public void setUp() {
    sender = "annie";
    receiverName = "zxy";
    message = "punch you in your face";
    right = new DirectMessage(sender, receiverName, message);
    backup = new DirectMessage(sender, receiverName, message);
    diff = new DirectMessage("sesame the cat", receiverName, message);
  }

  @Test
  void testGetSender() {
    assertEquals(sender, right.getSenderUsername());
  }

  @Test
  void testGetReceiver() {
    assertEquals(receiverName, right.getRecipientUsername());
  }

  @Test
  void testgetMessage() {
    assertEquals(message, right.getMessage());
  }

  @Test
  void testSend() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    right.send(dataOutputStream);

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    assertEquals(Protocol.DIRECT_MESSAGE, dataInputStream.readInt());
    assertEquals(sender, dataInputStream.readUTF());
    assertEquals(receiverName, dataInputStream.readUTF());
    assertEquals(message, dataInputStream.readUTF());
  }

  @Test
  void testReceive() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    dataOutputStream.writeUTF(sender);
    dataOutputStream.writeUTF(receiverName);
    dataOutputStream.writeUTF(message);
    dataOutputStream.flush();

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    DirectMessage receivedMessage = DirectMessage.receive(dataInputStream);
    assertEquals(sender, receivedMessage.getSenderUsername());
    assertEquals(receiverName, receivedMessage.getRecipientUsername());
    assertEquals(message, receivedMessage.getMessage());
  }

  @Test
  void testToString() {
    assertTrue(right.toString().contains("DirectMessage{messageType:"));
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

    DirectMessage zvalue = new DirectMessage(sender, receiverName, message);
    assertEquals(true,right.equals(zvalue));
    assertEquals(true,zvalue.equals(backup));

    assertEquals(false,right.equals(null));
    assertEquals(false,right.equals(diff));

    DirectMessage diffReceiver = new DirectMessage(sender, "ciba the cat", message);
    DirectMessage diffMessage = new DirectMessage(sender, receiverName, "Kick your butt");

    assertEquals(false,right.equals(diffReceiver));
    assertEquals(false,right.equals(diffMessage));
  }
}
