package QQService;

import QQCommon.Message;
import QQCommon.MessageType;
import QQCommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

public class QQServer {
    private ServerSocket ss = null;
    static HashMap<String, User> vaildUsers = new HashMap<>();
    static HashMap<String, Vector<Message>> offlineMessage = new HashMap<>();

    public static HashMap<String, Vector<Message>> getOfflineMessage() {
        return offlineMessage;
    }

    public static void setOfflineMessage(HashMap<String, Vector<Message>> offlineMessage) {
        QQServer.offlineMessage = offlineMessage;
    }

    static {
        vaildUsers.put("夜尘似幻", new User("夜尘似幻", "11112222"));
        vaildUsers.put("神里绫华", new User("神里绫华", "11112222"));
    }

    public static void main(String[] args) {
        new QQServer();
    }

    public static boolean checkUser(String userId, String passwd) {
        User user = vaildUsers.get(userId);
        if (user == null) return false;
        if (!user.getPasswd().equals(passwd)) return false;
        return true;
    }


    public QQServer() {
        System.out.println("服务端在9999号端口监听...");
        try {
            new Thread(new SendNewsToAllService()).start();
            ss = new ServerSocket(9999);
            while (true) //一直监听
            {
                Socket socket = ss.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User u = (User) ois.readObject();

                //创建信息，回复客户端
                Message message = new Message();
                if (checkUser(u.getUserId(), u.getPasswd())) {
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(message);

                    //创建一个线程，和客户端保持通讯，并持有socket
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());
                    serverConnectClientThread.start();
                    ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);
                } else {
                    System.out.println("用户 id=" + u.getUserId() + " pwd=" + u.getPasswd() + " 验证失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
