package QQClient.service;

import QQCommon.Message;
import QQCommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class MessageClientService {
    public void sendMessageToAll(String content, String senderId) {
        Message message = new Message();
        message.setSender(senderId);
        message.setMesType(MessageType.MESSAGE_TOALL_MES);
        message.setContent(content);
        message.setSendTime(new Date().toString());
        System.out.println("你对大家说：" + content);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToOne(String content, String senderId, String getterId) {
        Message message = new Message();
        message.setSender(senderId);
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setContent(content);
        message.setGetter(getterId);
        message.setSendTime(new Date().toString());
        System.out.println("你对 " + getterId + " 说：" + content);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
