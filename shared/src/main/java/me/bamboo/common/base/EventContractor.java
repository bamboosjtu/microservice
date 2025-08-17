package me.bamboo.common.base;

import java.time.Instant;
import java.util.UUID;

public interface EventContractor<T> {
	String version = "1.0";
	
	UUID getId();
	
	String getSource();

	String getType();
	
	String getVersion();
	
	Instant getCreated();
	
	UUID getCorrelationId();

	T getPayload();

	String getAggregateId();	

}
