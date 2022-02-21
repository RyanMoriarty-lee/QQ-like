package QQClient.service;

import QQCommon.Message;
import QQCommon.MessageType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread {
    private Socket socket;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ObjectInputStream ois;
        while (true) {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    String[] online = message.getContent().split(" ");
                    System.out.println("\n===========在线列表===========");

                    for (String str : online) {
                        System.out.println("用户：" + str);
                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    System.out.println("\n" + message.getSender() + " 对我说：" + message.getContent());
                } else if(message.getMesType().equals(MessageType.MESSAGE_TOALL_MES)){
                    System.out.println("\n" + message.getSender() + " 对大家说：" + message.getContent());
                }else if(message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    System.out.println("\n"+message.getSender()+" 给你发送了文件："+message.getSrc()+" 到："+message.getDest());
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n 文件保存成功");
                }
                else {
                    System.out.println("是其他类型的信息");
                }

            } catch (Exception e) {
                break;
            }

        }
    }
}
