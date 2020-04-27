package com.sa.client;

import java.net.InetSocketAddress;

import com.sa.base.CenterManager;
import com.sa.base.ConfManager;
import com.sa.base.ServerDataPool;
import com.sa.net.codec.PacketDecoder;
import com.sa.net.codec.PacketEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;

public class MonitorClient implements Runnable {
	private String name;
	private String host;
	private int port;

	/** 当前重接次数*/
	private int reconnectTimes = 0;
	public MonitorClient(String name, String host, int port) {
		this.name = name;
		this.host = host;
		this.port = port;
	}

	public void run() {
		try {
			connect(this.host, this.port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void connect(String host,int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);

        try{  
        	Bootstrap b  = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>(){  
  
                @Override  
                protected void initChannel(SocketChannel arg0)
                        throws Exception {  
                    ChannelPipeline pipeline = arg0.pipeline();  
                    pipeline.addLast(new PacketDecoder(1024*100, 0,4,0,4));  
                    pipeline.addLast(new LengthFieldPrepender(4));  
                    pipeline.addLast(new PacketEncoder());
                    pipeline.addLast(new HeartBeatHandler());
                    pipeline.addLast(new ClientTransportHandler());  
                }  
                  
            });  
            ChannelFuture f = b.connect(new InetSocketAddress(host, port)).sync();
            // 重连成功，重置重连次数
            if(f.channel().isActive()){
            	resetReconnectTimes();
            }
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();  
        } finally {
        	//设置最大重连次数，防止服务端正常关闭导致的空循环
        	if (reconnectTimes < ConfManager.getMaxReconnectTimes()) {
                reconnectTimes++;
                ServerDataPool.threadManager.setNum(this.name, reconnectTimes);
        		reConnectServer();
        	} else {
        		System.out.println("开始主备切换");
//        		new CenterManager().activeStandbySwitching();
        	}

        }  
    }  
      
    /** 
     * 断线重连 
     */  
    private void reConnectServer() {
    	try {
			connect(this.host, this.port);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
      
    /**
     * 重置重连次数
     */
    public void resetReconnectTimes() {
    	if (reconnectTimes > 0) {
    		reconnectTimes = 0;
    		ServerDataPool.threadManager.setNum(this.name, reconnectTimes);
    		System.err.println("断线重连成功");
    	}
    }
}