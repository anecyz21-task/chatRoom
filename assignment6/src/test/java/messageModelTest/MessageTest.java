package messageModelTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import messageModel.ConnectMessage;
import messageModel.Message;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class MessageTest {
  @Test
  public void testGetMessageType() {
    Message message = new ConcreteMessage(Protocol.CONNECT_MESSAGE);
    assertEquals(Protocol.CONNECT_MESSAGE, message.getMessageType());
  }

  @Test
  public void testReceiveValidTypes() throws IOException {
    for (int type : new int[]{Protocol.CONNECT_MESSAGE, Protocol.DISCONNECT_MESSAGE, Protocol.QUERY_CONNECTED_USERS,
        Protocol.BROADCAST_MESSAGE, Protocol.DIRECT_MESSAGE, Protocol.SEND_INSULT, Protocol.FAILED_MESSAGE}) {
      byte[] inputData = createMessageWithType(type);
      DataInputStream in = new DataInputStream(new ByteArrayInputStream(inputData));
    }
  }

  @Test
  public void testReceiveUnknownType() throws IOException {
    byte[] inputData = createMessageWithType(999);
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(inputData));
  }

  @Test
  public void testEqualsAndHashCode() {
    Message m1 = new ConcreteMessage(Protocol.CONNECT_MESSAGE);
    Message m2 = new ConcreteMessage(Protocol.CONNECT_MESSAGE);
    Message m3 = new ConcreteMessage(Protocol.DISCONNECT_MESSAGE);

    assertTrue(m1.equals(m2));
    assertEquals(m1.hashCode(), m2.hashCode());

    assertFalse(m1.equals(m3));
  }

  @Test
  public void testToString() {
    Message message = new ConcreteMessage(Protocol.CONNECT_MESSAGE);
    assertEquals("Message{messageType:" + Protocol.CONNECT_MESSAGE + "}", message.toString());
  }

  private byte[] createMessageWithType(int messageType) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(messageType);
    return baos.toByteArray();
  }

  private static class ConcreteMessage extends Message {
    public ConcreteMessage(int type) {
      this.messageType = type;
    }

    @Override
    public void send(DataOutputStream out) throws IOException {
    }
  }
}
