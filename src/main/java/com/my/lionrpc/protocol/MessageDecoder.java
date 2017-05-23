package com.my.lionrpc.protocol;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.pool.KryoPool;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by littlechen on 17/5/21.
 */
public class MessageDecoder extends ByteToMessageDecoder{
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageDecoder.class);

    private Class<?> clazz;
    
    private KryoPool pool;

    public MessageDecoder(Class<?> clazz){
        this.clazz = clazz;
        pool = new KryoPool.Builder(new MyKryoFactory()).build();
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        if(in.readableBytes() < 4){
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        
        Kryo kryo = null;
        try{
	        kryo = pool.borrow();
	        Input input = new Input(data);
	        Object obj = kryo.readObject(input,this.clazz);
	        out.add(obj);
        }catch(Exception e){
        	LOG.warn("MessageDecoder happen exception.",e);
        }finally{
        	if(kryo != null){
        		pool.release(kryo);
        	}
        }
    }
}
