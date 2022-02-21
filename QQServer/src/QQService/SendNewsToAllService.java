package QQService;

import QQCommon.Message;
import QQCommon.MessageType;
import utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class SendNewsToAllService implements Runnable{
    @Override
    public void run() {
        while(true)
        {
            System.out.println("请输入服务器要推送的消息[输入exit表示退出推送服务]");
            String news = Utility.readString(100);
            if(news.equals("exit")) break;
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setSendTime(new Date().toString());
            message.setMesType(MessageType.MESSAGE_TOALL_MES);
            System.out.println("服务器对所有人说："+news);

            HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
            for(String userId:hm.keySet()){
                ServerConnectClientThread serverConnectClientThread=hm.get(userId);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
