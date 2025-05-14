package messageModel;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;
import protocol.Protocol;
/**
 * Represents a DisconnectResp object containing whether the disconnection request is successful and a message
 * indicating more detailed information
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class DisconnectResp extends Message {
  private boolean success;
  private String message;

  /**
   * Construct a DisconnectResp object containing whether the disconnection request is successful and a message
   * indicating more detailed information
   *
   * @param success disconnection request result
   * @param message detailed information describing the disconnection response
   */
  public DisconnectResp(boolean success, String message) {
    this.messageType = Protocol.OTHER;
    this.success = success;
    this.message = message;
  }

  /**
   *
   * @return disconnection request result
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   *
   * @return detailed information describing the disconnection response
   */
  public String getMessage() {
    return message;
  }

  /**
   * Send the disconnection request result and connection detailed information
   * to the given output stream
   *
   * @param out the given output stream
   * @throws IOException if the provided output stream is invalid
   */
  @Override
  public void send(DataOutputStream out) throws IOException {
    out.writeInt(messageType);
    out.writeBoolean(success);
    out.writeUTF(message);
    out.flush();
  }

  /**
   * receive a disconnection request result and disconnection detailed information
   * and return a DisconnectResp object containing the request result and detailed information
   *
   * @param in the given input stream
   * @return  a new DisconnectResp object containing the request result and detailed information
   * @throws IOException if the given input stream is invalid
   */
  public static DisconnectResp receive(DataInputStream in) throws IOException {
    boolean success = in.readBoolean();
    String message = in.readUTF();
    return new DisconnectResp(success, message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DisconnectResp that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(isSuccess(), that.isSuccess()) && Objects.equals(getMessage(), that.getMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), isSuccess(), getMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "DisconnectResp{messageType:" + super.messageType + ", success: " + this.success + ", message: " + this.message + "}";
  }
}
