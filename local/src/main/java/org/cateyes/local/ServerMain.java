package org.cateyes.local;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class ServerMain {

    private final int port;

    public ServerMain(int port) {
        this.port = port;
    }

    public void run() {
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new ServerPipelineFactory());
        System.out.println("binding port : "+port);
        bootstrap.bind(new InetSocketAddress(port));
    }

    public static void main(String[] args) {
        new ServerMain(8081).run();
    }

}
