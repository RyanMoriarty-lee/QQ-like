package QQService;

import QQCommon.Message;
import QQCommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;
    private HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
    private HashMap<String, Vector<Message>> offlineMessage = QQServer.getOfflineMessage();


    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("服务端和客户端 " + userId + " 保持通讯，读取数据...");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                //申请在线列表
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                    System.out.println(message.getSender() + " 申请在线列表");
                    String onlineUser = ManageClientThreads.getOnlineUser();
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message2.setContent(onlineUser);
                    message2.setGetter(message.getSender());

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);
                }
                //客户端退出
                else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    System.out.println(message.getSender() + " 退出");
                    ManageClientThreads.remove(message.getSender());
                    socket.close();
                    break;
                }
                //私聊
                else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    boolean flag = true;
                    for (String userId : hm.keySet()) {
                        if (userId.equals(message.getGetter())) {
                            ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                            ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.socket.getOutputStream());
                            oos.writeObject(message);
                            flag = false;
                            break;
                        }
                    }

                    if (flag) {
                        if (offlineMessage.get(message.getGetter()) != null) {
                            offlineMessage.get(message.getGetter()).add(message);
                        } else {
                            Vector<Message> messages = new Vector<>();
                            messages.add(message);
                            offlineMessage.put(message.getGetter(), messages);
                        }
                    }
                }
                //群发
                else if (message.getMesType().equals(MessageType.MESSAGE_TOALL_MES)) {
                    Set<String> strings = QQServer.vaildUsers.keySet();
                    for (String userId : strings) {
                        if (!userId.equals(message.getSender())) {
                            boolean flag = true;
                            for (String userExsist : hm.keySet()) {
                                if (userId.equals(userExsist)) {
                                    ObjectOutputStream oos = new ObjectOutputStream(hm.get(userId).getSocket().getOutputStream());
                                    oos.writeObject(message);
                                    flag = false;
                                    break;
                                }
                            }
                            if (offlineMessage.get(userId) != null) {
                                offlineMessage.get(userId).add(message);
                            } else {
                                Vector<Message> messages = new Vector<>();
                                messages.add(message);
                                offlineMessage.put(userId, messages);
                            }
                        }
                    }
                }
                //发文件
                else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
//                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
//                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
//                    oos.writeObject(message);
                    boolean flag = true;
                    for (String userId : hm.keySet()) {
                        if (userId.equals(message.getGetter())) {
                            ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                            ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.socket.getOutputStream());
                            oos.writeObject(message);
                            flag = false;
                            break;
                        }
                    }

                    if (flag) {
                        if (offlineMessage.get(message.getGetter()) != null) {
                            offlineMessage.get(message.getGetter()).add(message);
                        } else {
                            Vector<Message> messages = new Vector<>();
                            messages.add(message);
                            offlineMessage.put(message.getGetter(), messages);
                        }
                    }
                } //离线
                else if (message.getMesType().equals(MessageType.MESSAGE_OFF_MES)) {
                    if (offlineMessage.get(message.getSender()) != null) {
                        ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getServerConnectClientThread(message.getSender());
//                      ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        while (offlineMessage.get(message.getSender()).size() != 0) {
                            Message message1 = offlineMessage.get(message.getSender()).get(0);
                            ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                            oos.writeObject(message1);
                            offlineMessage.get(message.getSender()).remove(0);
                        }
                        offlineMessage.remove(message.getSender());
                    }


                } else {
                    System.out.println("其他类型");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
