package me.bamboo.common.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.NonNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        visible = true,
        property = "eventType"
//        defaultImpl = AccountEvent.DefaultAccountEvent.class
)
@JsonSubTypes({@JsonSubTypes.Type(AccountCreatedEvent.class)})
public abstract class AccountEvent {
	@NonNull
	protected String eventType;
	
	public AccountEvent(AccountEvent.EventType eventType) {
        this.eventType = eventType.getEventName();
    }
	
	private AccountEvent() {
    }
	
	@JsonIgnore
    public String getEventName() {
        return this.eventType;
    }
	
	public static enum EventType {
        ACCOUNT_CREATED("AccountCreated");

        private final String eventName;

        EventType(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return this.eventName;
        }
    }
	
//	protected static class DefaultAccountEvent extends AccountEvent {
//        protected DefaultAccountEvent() {
//        }
//
//        public boolean equals(Object o) {
//            if (o == this) {
//                return true;
//            } else if (!(o instanceof AccountEvent.DefaultAccountEvent)) {
//                return false;
//            } else {
//                AccountEvent.DefaultAccountEvent other = (AccountEvent.DefaultAccountEvent)o;
//                if (!other.canEqual(this)) {
//                    return false;
//                } else {
//                    return super.equals(o);
//                }
//            }
//        }
//
//        protected boolean canEqual(Object other) {
//            return other instanceof DefaultAccountEvent.DefaultAccountEvent;
//        }
//
//        public int hashCode() {
//            int result = super.hashCode();
//            return result;
//        }
//    }

}
