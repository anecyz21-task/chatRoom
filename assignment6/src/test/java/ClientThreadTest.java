import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import messageModel.FailedMessage;
import messageModel.Message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import protocol.Protocol;

public class ClientThreadTest {

  private ClientThread clientThread;
  private ByteArrayOutputStream byteOut;
  private DataOutputStream dataOut;

  @BeforeEach
  void setUp() {
    byteOut = new ByteArrayOutputStream();
    dataOut = new DataOutputStream(byteOut);
    clientThread = new ClientThread(dataOut, "testUser");
  }

  @Test
  void testConstructorAndGetters() {
    assertEquals("testUser", clientThread.getUserName());
    assertEquals("testUser", clientThread.getUsername());
    assertEquals(dataOut, clientThread.getOutStream());

    BlockingQueue<Message> queue = clientThread.getMessageLine();
    assertNotNull(queue);
    assertTrue(queue.isEmpty());
  }

  @Test
  void testReceiveMessage() {
    FailedMessage msg = new FailedMessage("TestError");
    clientThread.receiveMessage(msg);
    assertFalse(clientThread.getMessageLine().isEmpty());
    assertEquals(msg, clientThread.getMessageLine().peek());
  }

  @Test
  void testRunMethod() throws InterruptedException, IOException {
    FailedMessage testMessage = new FailedMessage("RunTestError");

    Thread t = new Thread(clientThread);
    t.start();

    clientThread.receiveMessage(testMessage);
    Thread.sleep(200);
    assertTrue(clientThread.getMessageLine().isEmpty());
    t.interrupt();
    t.join(500);
    DataInputStream dataIn = new DataInputStream(new java.io.ByteArrayInputStream(byteOut.toByteArray()));

    int messageType = dataIn.readInt();
    String errorMsg = dataIn.readUTF();

    assertEquals(Protocol.FAILED_MESSAGE, messageType);
    assertEquals("RunTestError", errorMsg);
  }

  @Test
  void testToString() {
    String str = clientThread.toString();
    assertTrue(str.contains("username: testUser"));
    assertTrue(str.contains("current message queue:"));
    assertTrue(str.contains("output stream:"));
  }

  @Test
  void testEqualsAndHashCode() {
    assertEquals(clientThread, clientThread);

    ClientThread sameFields = new ClientThread(dataOut, "testUser");
    assertEquals(clientThread, sameFields);
    assertEquals(clientThread.hashCode(), sameFields.hashCode());

    ClientThread diffUser = new ClientThread(dataOut, "otherUser");
    assertNotEquals(clientThread, diffUser);

    ByteArrayOutputStream anotherByteOut = new ByteArrayOutputStream();
    DataOutputStream anotherDataOut = new DataOutputStream(anotherByteOut);
    ClientThread diffOutStream = new ClientThread(anotherDataOut, "testUser1");
    assertNotEquals(clientThread, diffOutStream);

    assertNotEquals(clientThread, null);
    assertNotEquals(clientThread, "A String");
  }

  @Test
  void testHashCode() {
    assertEquals(clientThread.hashCode(), clientThread.hashCode());
  }
}
