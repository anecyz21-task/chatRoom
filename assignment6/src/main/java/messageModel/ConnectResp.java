package messageModel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import protocol.Protocol;
/**
 * Represents a ConnectResp object containing whether the connection request is successful and a message
 * indicating more detailed information
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class ConnectResp extends Message {
  private boolean isSuccess;
  private String message;

  /**
   * Construct a ConnectResp object containing whether the connection request is successful and a message
   * indicating more detailed information
   *
   * @param success connection request result
   * @param message detailed information describing the connection response
   */
  public ConnectResp(boolean success, String message) {
    this.messageType = Protocol.CONNECT_RESPONSE;
    this.isSuccess = success;
    this.message = message;
  }

  /**
   *
   * @return connection request result
   */
  public boolean getSuccess() {
    return isSuccess;
  }

  /**
   *
   * @return detailed information describing the connection response
   */
  public String getMessage() {
    return message;
  }

  /**
   * Send the connection request result and connection detailed information
   * to the given output stream
   *
   * @param out the given output stream
   * @throws IOException if the provided output stream is invalid
   */
  @Override
  public void send(DataOutputStream out) throws IOException {
    out.writeInt(messageType);
    out.writeBoolean(isSuccess);
    out.writeUTF(message);
    out.flush();
  }

  /**
   * receive a connection request result and connection detailed information
   * and return a ConnectResp object containing the request result and detailed information
   *
   * @param in the given input stream
   * @return  a new ConnectResp object containing the request result and detailed information
   * @throws IOException if the given input stream is invalid
   */
  public static ConnectResp receive(DataInputStream in) throws IOException {
    int msgType = in.readInt();
    boolean success = in.readBoolean();
    String message = in.readUTF();
    return new ConnectResp(success, message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConnectResp that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return getSuccess() == that.getSuccess() && Objects.equals(getMessage(),
        that.getMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getSuccess(), getMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ConnectResp{messageType:" + super.messageType + ", success:" + isSuccess + ", message:" + message + "}";
  }
}
