package QQClient.service;

import java.util.HashMap;

public class ManageClientConnectServerThread {
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();
    public static void addClientConnectServerThread(String userId,ClientConnectServerThread clientConnectServerThread){
        hm.put(userId,clientConnectServerThread);
    }

    public static ClientConnectServerThread getClientConnectServerThread(String userId){
        return hm.get(userId);
    }

    public static void remove(String userId)
    {
        hm.remove(userId);
    }
}
