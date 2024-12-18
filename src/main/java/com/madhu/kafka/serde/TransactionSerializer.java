package com.madhu.kafka.serde;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.kafka.common.serialization.Serializer;

import com.madhu.kafka.transaction.Transaction;

public class TransactionSerializer implements Serializer<Transaction>{
	 @Override
	    public byte[] serialize(String topic, Transaction data) {
	        try {
	            ByteArrayOutputStream bao = new ByteArrayOutputStream();
	            ObjectOutputStream oos = new ObjectOutputStream(bao);
	            oos.writeObject(data);
	            oos.close();
	            return bao.toByteArray();
	        } catch (IOException e) {
	            throw new RuntimeException("Error serializing Transaction message", e);
	        }
	    }
}
