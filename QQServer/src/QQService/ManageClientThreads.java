package QQService;

import java.util.HashMap;

public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread){
        hm.put(userId,serverConnectClientThread);
    }

    public static ServerConnectClientThread getServerConnectClientThread(String userId){
        return hm.get(userId);
    }

    public static String getOnlineUser(){
        String ret="";
        for (String str:hm.keySet()){
            ret+=str+" ";
        }
        return ret;
    }

    public static void remove(String userId){
        hm.remove(userId);
    }
}
