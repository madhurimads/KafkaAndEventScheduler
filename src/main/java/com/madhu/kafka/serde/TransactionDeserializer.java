package com.madhu.kafka.serde;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.kafka.common.serialization.Deserializer;

import com.madhu.kafka.transaction.Transaction;

public class TransactionDeserializer implements Deserializer<Transaction>{

	@Override
	public Transaction deserialize(String topic, byte[] data) {

        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            Object obj = ois.readObject();

            if (obj instanceof Transaction) {
            	return (Transaction) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Transaction("111", 30);
	}

}
