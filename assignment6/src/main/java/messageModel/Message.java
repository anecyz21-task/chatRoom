package messageModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import protocol.Protocol;
/**
 * Represents an abstract Message object in the protocol communication.
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public abstract class Message {

  /**
   * protocol message type
   */
  protected int messageType;

  /**
   *
   * @return the message type of the message
   */
  public int getMessageType() {
    return messageType;
  }

  /**
   * send the content of the message object to the provided output stream
   *
   * @param out the given output stream
   * @throws IOException if the provided output stream is invalid
   */
  public abstract void send(DataOutputStream out) throws IOException;

  /**
   * receive information from given input stream and return a new object containing the received
   * information in an easy and readable way
   *
   * @param in the given input stream
   * @return  a new message object containing the received errormessage
   * @throws IOException if the given input stream is invalid
   */
  public static Message receive(DataInputStream in) throws IOException {
    int messageType = in.readInt();

    switch (messageType) {
      case Protocol.CONNECT_MESSAGE:
        return ConnectMessage.receive(in);
      case Protocol.DISCONNECT_MESSAGE:
        return DisconnectMessage.receive(in);
      case Protocol.QUERY_CONNECTED_USERS:
        return QueryUsers.receive(in);
      case Protocol.BROADCAST_MESSAGE:
        return BroadcastMessage.receive(in);
      case Protocol.DIRECT_MESSAGE:
        return DirectMessage.receive(in);
      case Protocol.SEND_INSULT:
        return InsultMessage.receive(in);
      case Protocol.FAILED_MESSAGE:
        return FailedMessage.receive(in);
      default:
        throw new IOException("Unknown message type: " + messageType);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Message message = (Message) o;
    return messageType == message.messageType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return messageType * 31;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "Message{messageType:" + messageType + "}";
  }
}
