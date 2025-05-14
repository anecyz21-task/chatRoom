import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import messageModel.BroadcastMessage;
import messageModel.ConnectMessage;
import messageModel.ConnectResp;
import messageModel.DirectMessage;
import messageModel.DisconnectMessage;
import messageModel.DisconnectResp;
import messageModel.FailedMessage;
import messageModel.InsultMessage;
import messageModel.Message;
import messageModel.QueryResp;
import messageModel.QueryUsers;
import protocol.Protocol;

/**
 * Represents a ChatServer to hold a chat room for up to 10 Client users
 *
 * @author Yezhen Chen, Xiaoyu Zhou
 * @version 0.01 12/6/24
 */
public class ChatServer {

  private static final int MAX_CLIENTS = 10;
  private int port;
  private ServerSocket serverSocket;
  private ExecutorService threadPool;
  private InsultGenerator insultGenerator;
  private ConcurrentHashMap<String, ClientThread> clients;

  /**
   * Create a new chat room hold on this ChatServer by given port
   * @param port given port number
   */
  public ChatServer(int port) {
    this.port = port;
    clients = new ConcurrentHashMap<>();
    insultGenerator = new InsultGenerator();
    threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
  }

  /**
   * Start this chat server
   * @throws IOException will be ignored for unexpected input
   */
  public void start() throws IOException {
    serverSocket = new ServerSocket(port);
    System.out.println("Server listening on port: " + port);
    while (true) {
      Socket clientSocket = serverSocket.accept();
      DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
      Message initialMsg = Message.receive(dataIn);

      ConnectMessage connectMsg = (ConnectMessage) initialMsg;
      String username = connectMsg.getUsername().trim();

      ClientThread clientThread = new ClientThread(dataOut, username);
      clients.put(username, clientThread);
      String successMsg = "Log in as username: " + username + ".\n";
      ConnectResp connectResponse = new ConnectResp(true, successMsg);
      connectResponse.send(dataOut);
      threadPool.execute(clientThread);

      Runnable readerTask = () -> {
        try {
          while (true) {
            Message msg = Message.receive(dataIn);
            handleMessage(msg, username, clientSocket, dataOut);
          }
        } catch (IOException e) {
          System.out.println("Client " + username + " disconnected.");
        } finally {
          clients.remove(username);
          try {
            clientSocket.close();
          } catch (IOException ignored) {
          }
        }
      };

      threadPool.execute(readerTask);
    }
  }

  /**
   * Process given message based on their type
   * @param message given by user
   * @param username of the user
   * @param clientSocket of the user
   * @param dataOut of the user
   * @throws IOException will be ignored for unexpected input
   */
  private void handleMessage(Message message, String username, Socket clientSocket, DataOutputStream dataOut) throws IOException {
    switch (message.getMessageType()) {
      case Protocol.DISCONNECT_MESSAGE:
        handleDisconnectMessage((DisconnectMessage) message, username, clientSocket, dataOut);
        break;
      case Protocol.BROADCAST_MESSAGE:
        handleBroadcastMessage((BroadcastMessage) message, username, dataOut);
        break;
      case Protocol.DIRECT_MESSAGE:
        handleDirectMessage((DirectMessage) message, username, dataOut);
        break;
      case Protocol.SEND_INSULT:
        handleSendInsultMessage((InsultMessage) message, username, dataOut);
        break;
      case Protocol.QUERY_CONNECTED_USERS:
        handleQueryUsersMessage((QueryUsers) message, username, dataOut);
        break;
      default:
        sendFailedMessage(dataOut, "Unsupported message type: " + message.getMessageType());
        break;
    }
  }

  /**
   * Process disconnect message
   * @param msg given by user
   * @param username of the user
   * @param clientSocket of the user
   * @param dataOut of the user
   * @throws IOException will be ignored for unexpected input
   */
  private void handleDisconnectMessage(DisconnectMessage msg,
      String username, Socket clientSocket, DataOutputStream dataOut) throws IOException {
    if (!clients.containsKey(username)) {
      sendFailedMessage(dataOut, "You are not connected.");
      return;
    }

    clients.remove(username);
    DisconnectResp response = new DisconnectResp(true, "You are no longer connected.");
    response.send(dataOut);

    clientSocket.close();
  }

  /**
   * Process broadcast message
   * @param msg given by user
   * @param username of the user
   * @param dataOut of the user
   * @throws IOException will be ignored for unexpected input
   */
  private void handleBroadcastMessage(BroadcastMessage msg, String username, DataOutputStream dataOut) throws IOException {
    if (!clients.containsKey(username)) {
      return;
    }
    broadcastMessage(msg, dataOut);
  }

  /**
   * Process Direct Message
   * @param msg given by user
   * @param username of the user
   * @param dataOut of the user
   * @throws IOException will be ignored for unexpected input
   */
  private void handleDirectMessage(DirectMessage msg, String username, DataOutputStream dataOut) throws IOException {
    if (!clients.containsKey(username)) {
      sendFailedMessage(dataOut, "You are not connected.");
      return;
    }
    String recipient = msg.getRecipientUsername();
    if (!clients.containsKey(recipient)) {
      sendFailedMessage(dataOut, "Recipient not found: " + recipient);
      return;
    }
    sendDirectMessage(msg);
  }

  /**
   * Process Direct Message
   * @param message given by user
   */
  private void sendDirectMessage(DirectMessage message) {
    ClientThread ct = clients.get(message.getRecipientUsername());
    ct.receiveMessage(message);
  }

  /**
   * Process Insult Message
   * @param msg given by user
   * @param username of the user
   * @param dataOut of the user
   * @throws IOException will be ignored for unexpected input
   */
  private void handleSendInsultMessage(InsultMessage msg, String username, DataOutputStream dataOut) throws IOException {
    if (!clients.containsKey(username)) {
      ClientThread sender = clients.get(username);
      sender.receiveMessage(msg);
      return;
    }

    String recipient = msg.getRecipientUsername();

    InsultGenerator generator = new InsultGenerator();
    String insult = generator.generate();
    String formattedInsult = username + " -> " + recipient + ": " + insult + "\n";

    broadcastMessage(new BroadcastMessage(msg.getSenderUsername(), formattedInsult), dataOut);
  }

  /**
   * Process query user message
   * @param msg given by user
   * @param username of the user
   * @param dataOut of the user
   * @throws IOException will be ignored for unexpected input
   */
  private void handleQueryUsersMessage(QueryUsers msg, String username, DataOutputStream dataOut) throws IOException {
    if (!clients.containsKey(username)) {
      sendFailedMessage(dataOut, "You are not connected.");
      return;
    }
    List<String> users = new ArrayList<>(clients.keySet());
    users.remove(username);
    QueryResp response = new QueryResp(users);
    response.send(dataOut);
  }

  /**
   * Process broadcast message
   * @param msg given by user
   * @param out of the user
   * @throws IOException will be ignored for unexpected input
   */
  public void broadcastMessage(BroadcastMessage msg, DataOutputStream out) throws IOException {
    for (String user : clients.keySet()) {
      if (msg.getSenderUsername() != null && msg.getSenderUsername().equals(user)) {
        new FailedMessage("").send(out);
        continue;
      }
      clients.get(user).receiveMessage(msg);
    }
  }

  /**
   * Process fail message
   * @param dataOut of the user
   * @param errorMessage will be sent to user
   * @throws IOException will be ignored for unexpected input
   */
  private void sendFailedMessage(DataOutputStream dataOut, String errorMessage) throws IOException {
    FailedMessage fm = new FailedMessage(errorMessage);
    fm.send(dataOut);
  }

  /**
   *
   * @return port of this server
   */
  public int getPort() {
    return port;
  }

  /**
   *
   * @return serverSocket of this server
   */
  public ServerSocket getServerSocket() {
    return serverSocket;
  }

  /**
   *
   * @return threadPool of this server
   */
  public ExecutorService getThreadPool() {
    return threadPool;
  }

  /**
   *
   * @return InsultGenerator of this server
   */
  public InsultGenerator getInsultGenerator() {
    return insultGenerator;
  }

  /**
   *
   * @return clients of this server
   */
  public ConcurrentHashMap<String, ClientThread> getClients() {
    return clients;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ChatServer that)) {
      return false;
    }
    return getPort() == that.getPort() && Objects.equals(getServerSocket(),
        that.getServerSocket())
        && Objects.equals(getClients(), that.getClients());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(getPort(), getServerSocket(),
        getClients());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ChatServer{" +
        "port=" + port +
        ", serverSocket=" + serverSocket +
        ", clients=" + clients +
        '}';
  }

  /**
   * main of server
   * @param args from users
   */
  public static void main(String[] args) {
    int port = 18888;
    ChatServer server = new ChatServer(port);
    try {
      server.start();
    } catch (IOException e) {
      throw new RuntimeException("Failed to start chat server", e);
    }
  }
}
