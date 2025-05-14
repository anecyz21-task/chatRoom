import java.io.*;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import messageModel.Message;

/**
 * Represents a ClientThread object that receives direct or group messages from other users and transfer
 * the messages to the user client to show to the user
 */
public class ClientThread implements Runnable {
  private DataOutputStream outStream;
  private String userName;
  private BlockingQueue<Message> messageLine;

  /**
   * Construct a ClientThread object that receives direct or group messages from other users and transfer
   * the messages to the user client to show to the user
   *
   * @param outStream the output stream to sent message to the user client
   * @param userName the username of the user receiving messages
   */
  public ClientThread(DataOutputStream outStream, String userName) {
    this.outStream = outStream;
    this.userName = userName;
    this.messageLine = new LinkedBlockingQueue<>() {
    };
  }

  /**
   *
   * @return the username of the user receiving messages
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * start the thread to send received message to the user client
   */
  public void run() {
    while (true) {
      if (this.messageLine.size() > 0) {
        try {
          this.messageLine.remove().send(outStream);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  /**
   * Receive message from chat server to be prepared to send to user client
   *
   * @param message message received from chat server
   */
  public void receiveMessage(Message message) {
    this.messageLine.add(message);
  }

  /**
   *
   * @return the output stream to sent message to the user client
   */
  public DataOutputStream getOutStream() {
    return this.outStream;
  }

  /**
   *
   * @return the messages that will be sent to the user client
   */
  public BlockingQueue<Message> getMessageLine() {
    return this.messageLine;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "username: "+ this.userName + ", current message queue: " + this.messageLine.toString() + ", output stream: " + this.outStream.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = userName.hashCode() * 31;
    for (Message msg : messageLine) {
      result = 31 * result + (msg == null ? 0 : msg.hashCode());
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientThread that = (ClientThread) o;
    boolean a = this.userName.equals(that.getUserName());
    boolean b = this.messageLine.toString().equals(that.getMessageLine().toString());
    return a &&
        b;
  }
}
