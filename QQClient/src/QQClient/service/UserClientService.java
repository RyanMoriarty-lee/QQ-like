package QQClient.service;

import QQCommon.Message;
import QQCommon.MessageType;
import QQCommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class UserClientService {
    private User u = new User();
    private Socket socket = null;

    public boolean checkUser(String userId, String pwd) throws IOException, ClassNotFoundException {
        boolean b = false;
        u.setUserId(userId);
        u.setPasswd(pwd);

        socket = new Socket(InetAddress.getLocalHost(), 9999);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(u);

        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Message ms = (Message) ois.readObject();

        if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) { //登陆成功
            //创建一个和服务器保持通讯的线程-》 创建一个类 ClientConnectServerThread
            ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
            clientConnectServerThread.start();
            //将线程加入管理类中
            ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);

            //申请离线数据
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Message message = new Message();
            message.setMesType(MessageType.MESSAGE_OFF_MES);
            message.setSender(userId);
            objectOutputStream.writeObject(message);


            b = true;

        } else {
            socket.close();
        }

        return b;
    }

    public void onlineFriendList() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());
        try {
            ObjectOutputStream oos = new ObjectOutputStream
                    (socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());

        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId() + " 退出系统");
            ManageClientConnectServerThread.remove(u.getUserId());
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
