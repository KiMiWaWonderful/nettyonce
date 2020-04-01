package main;

import server.NettyServer;

public class ServerMain {

    public static void main(String[] args) {
        NettyServer.startServer(9000);
    }
}
