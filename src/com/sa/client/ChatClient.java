package com.sa.client;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sa.base.ConfManager;
import com.sa.net.codec.PacketDecoder;
import com.sa.net.codec.PacketEncoder;
import com.sa.util.JedisUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
  
  
public class ChatClient implements Runnable {
	private String host;
	private int port;
	private boolean isMaster=false;
	/** 当前重接次数*/
	private int reconnectTimes = 0;
	
	public ChatClient(String host,int port,boolean isMaster) {
		this.host = host;
		this.port = port;
		this.isMaster = isMaster;
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
                    pipeline.addLast(new PacketDecoder(1024*1, 0,4,0,4));  
                    pipeline.addLast(new LengthFieldPrepender(4));  
                    pipeline.addLast(new PacketEncoder());
                    pipeline.addLast(new HeartBeatHandler());
                    pipeline.addLast(new ClientTransportHandler());  
                }  
                  
            });  
//            ChannelFuture f = b.connect(new InetSocketAddress(host, port),  
//                    new InetSocketAddress(ClientConfigs.LOCAL_SERVER_IP, ClientConfigs.LOCAL_SERVER_PORT))  
//                    .sync();
            ChannelFuture f = b.connect(new InetSocketAddress(host, port)).sync();
            //重连成功，重置重连次数
            if(f.channel().isActive()){
            	resetReconnectTimes();
            }
            f.channel().closeFuture().sync();  
        } catch (Exception e) {
            e.printStackTrace();  
        } finally {
//          group.shutdownGracefully();  //这里不再是优雅关闭了

        	//设置最大重连次数，防止服务端正常关闭导致的空循环
        	if (reconnectTimes < ConfManager.getMaxReconnectTimes()) {
                reconnectTimes++;
        		reConnectServer(reconnectTimes);
        		//重连最终失败，校验自己的角色,如果是备中心，说明主中心挂掉 
        		if(!isMaster){
        			//1.去redis将自己改为主
    				Map<String,String> centerRoleMap = new HashMap<>();
    				centerRoleMap.put("master", ConfManager.getCenterIp()+":"+ConfManager.getClientSoketServerPort());
    				centerRoleMap.put("slave", ConfManager.getCenterIpAnother()+":"+ConfManager.getCenterPortAnother());
    				JedisUtil jedisUtil = new JedisUtil();
    				jedisUtil.setHashMulti("centerRoleInfo", centerRoleMap);
    				isMaster = true;
            		//2.主动连接服务
        	        String[] address = ConfManager.getServerAddress();
        	        for (int i = 0; i < address.length; i++) {
        	        	String[] addr = address[i].split(":");
        	        	new Thread(new ChatClient(addr[0], Integer.valueOf(addr[1]),isMaster)).start();
        	       }
        		}
        	}

        }  
    }  
      
    /** 
     * 断线重连 
     */  
    private void reConnectServer(int reconnectTimes){
          
        try {  
            Thread.sleep(2000);
            String logStr = "";
            //因为是中心，所以是主重连服务，或备重连主
            if(host.equals(ConfManager.getCenterIpAnother())&&(port+"").equals(ConfManager.getCenterPortAnother())){
            	//备重连主
            	logStr ="备中心第"+reconnectTimes+"次断线重连主中心"+host+":"+port;
            }else{
            	//主重连服务
            	logStr ="主中心第"+reconnectTimes+"次断线重连服务"+host+":"+port;
            }
            System.err.println(logStr);
            //TODO断线重连警告
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
    		System.err.println("断线重连成功");
    	}
    }
    
      
}
