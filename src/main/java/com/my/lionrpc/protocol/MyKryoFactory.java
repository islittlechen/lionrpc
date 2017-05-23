package com.my.lionrpc.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;

public class MyKryoFactory implements KryoFactory{
	
	public Kryo create () {
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
		return kryo;
	}

}
