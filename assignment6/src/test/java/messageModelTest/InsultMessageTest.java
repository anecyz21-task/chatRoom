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
import messageModel.InsultMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class InsultMessageTest {
  private InsultMessage right;
  private InsultMessage diff;
  private InsultMessage backup;

  private String sender;
  private String receiver;

  @BeforeEach
  public void setUp() {
    sender = "annie";
    receiver = "zxy";
    right = new InsultMessage(sender,receiver);
    diff = new InsultMessage("sesame the cat", receiver);
    backup = new InsultMessage(sender,receiver);
  }

  @Test
  void testGetSender() {
    assertEquals(sender, right.getSenderUsername());
  }

  @Test
  void testGetReceiver() {
    assertEquals(receiver, right.getRecipientUsername());
  }

  @Test
  void testSend() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    right.send(dataOutputStream);

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    assertEquals(Protocol.SEND_INSULT, dataInputStream.readInt());
    assertEquals(sender, dataInputStream.readUTF());
    assertEquals(receiver, dataInputStream.readUTF());
  }

  @Test
  void testReceive() throws IOException {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);

    dataOutputStream.writeUTF(sender);
    dataOutputStream.writeUTF(receiver);
    dataOutputStream.flush();

    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());
    DataInputStream dataInputStream = new DataInputStream(byteInputStream);

    InsultMessage receivedMessage = InsultMessage.receive(dataInputStream);
    assertEquals(sender, receivedMessage.getSenderUsername());
    assertEquals(receiver, receivedMessage.getRecipientUsername());
  }

  @Test
  void testToString() {
    assertTrue(right.toString().contains("InsultMessage{messageType:"));
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

    InsultMessage zvalue = new InsultMessage(sender, receiver);
    assertEquals(true,right.equals(zvalue));
    assertEquals(true,zvalue.equals(backup));

    assertEquals(false,right.equals(null));
    assertEquals(false,right.equals(diff));
    InsultMessage diffReceiver = new InsultMessage(sender, "ciba the cat");
    assertEquals(false,right.equals(diffReceiver));
  }
}
