package QQClient.service;

import QQCommon.Message;
import QQCommon.MessageType;

import java.io.*;

public class FileClientService {
    public void sendFileOne(String src, String dest, String senderId, String getterId) {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("\n" + "你给 " + message.getGetter() + " 发送文件：" + src + " 对方的电脑目录：" + dest);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(message.getSender()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
