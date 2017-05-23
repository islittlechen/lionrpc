package com.my.lionrpc.protocol;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by littlechen on 17/5/21.
 */
@SuppressWarnings("rawtypes")
public class MessageEncoder extends MessageToByteEncoder{
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageEncoder.class);

    private Class<?> clazz;
    private KryoPool pool;
    
    public MessageEncoder(Class<?> clazz){
        this.clazz = clazz;
        pool = new KryoPool.Builder(new MyKryoFactory()).build();
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(clazz.isInstance(o)){
            Kryo kryo = null;
            try{
            	kryo = pool.borrow();
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
                Output output = new Output(baos);  
                kryo.writeObject(output, o);  
                output.flush();  
                output.close();  
               
                byte[] data = baos.toByteArray(); 
                byteBuf.writeInt(data.length);
                byteBuf.writeBytes(data);
                baos.close();
            }catch(Exception e){
            	LOG.warn("MessageEncoder happen exception.", e);
            }finally{
            	if(kryo != null){
            		 pool.release(kryo);
            	}
            }
            
        }

    }
}
