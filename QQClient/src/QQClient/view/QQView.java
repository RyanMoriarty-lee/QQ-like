package QQClient.view;

import QQClient.service.FileClientService;
import QQClient.service.MessageClientService;
import QQClient.service.UserClientService;
import QQClient.utils.Utility;

import java.io.IOException;

public class QQView {
    private boolean loop = true;
    private String key = "";
    private UserClientService userClientService = new UserClientService();
    private MessageClientService messageClientService = new MessageClientService();
    private FileClientService fileClientService = new FileClientService();


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new QQView().mainMenu();
        System.out.println("客户端退出系统......");
    }

    private void mainMenu() throws IOException, ClassNotFoundException {
        while (loop) {
            System.out.println("\n===========欢迎登陆===========");
            System.out.println("\t\t 1 登陆系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入选项：");
            key = Utility.readString(1);
            switch (key) {
                case "1":
                    System.out.print("请输入用户号：");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密码：");
                    String pwd = Utility.readString(50);

                    if (userClientService.checkUser(userId, pwd)) {
                        System.out.println("\n===========欢迎 (用户 " + userId + " 登陆成功)===========");
                        while (loop) {
                            System.out.println("===========二级菜单 (用户 " + userId + " )===========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出登录");
                            System.out.print("\n请输入你的选项：");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    userClientService.onlineFriendList();
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "2":
                                    System.out.print("请输入想对大家说的话：");
                                    String s = Utility.readString(50);
                                    messageClientService.sendMessageToAll(s, userId);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "3":
                                    System.out.print("请输入想聊天的用户号（在线）：");
                                    String getterId = Utility.readString(50);
                                    System.out.print("请输入想说的话：");
                                    String content = Utility.readString(100);
                                    messageClientService.sendMessageToOne(content, userId, getterId);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "4":
                                    System.out.println("请输入你想发送文件的用户");
                                    getterId = Utility.readString(50);
                                    System.out.println("请输入发送文件的路径");
                                    String src = Utility.readString(100);
                                    System.out.println("请输入发送到对方的路径");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileOne(src,dest,userId,getterId);
                                    break;
                                case "9":
                                    userClientService.logout();
                                    loop = false;
                                    break;
                            }
                        }
                    } else {
                        System.out.println("\n===========登陆失败===========");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }

        }
    }
}
