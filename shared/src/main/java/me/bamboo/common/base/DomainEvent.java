package me.bamboo.common.base;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString
@SuperBuilder(builderMethodName = "baseBuilder")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DomainEvent<T> implements EventContractor<T> {
	private UUID id;
    private String source;
    private String type;
    private String version = "v1";
    private Instant created;
    private UUID correlationId;
    private String aggregateId;
    private T payload;


	@Override
	public UUID getId() {
		return this.id;
	}

	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public Instant getCreated() {
		return this.created;
	}

	@Override
	public UUID getCorrelationId() {
		return this.correlationId;
	}
	
	@Override
	public String getAggregateId() {
		return this.aggregateId;
	}
	
	@Override
	public String getType() {
		return this.type;
	}
	
	@Override
	public T getPayload() {
		return this.payload;
	}
	
}
