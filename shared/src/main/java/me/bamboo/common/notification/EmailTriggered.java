package me.bamboo.common.notification;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import me.bamboo.common.book.Booktype;

@Getter
@JsonTypeName("EmailTriggered")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailTriggered extends NotificationEvent{
	private String firstName;
    private String lastName;
    private String email;
	private String title;
	private String author;
    private NotificationType notificationType;  
    
    @JsonCreator
    @ConstructorProperties({"firstName", "lastName", "email", "title", "author", "price", "types", "notificationType"})
    public EmailTriggered(String firstName, String lastName, String email, String title, String author, NotificationType notificationType) {
        super(EventType.EMAIL_TRIGGERED);
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.title = title;
        this.author = author;
        this.notificationType = notificationType;
    }

	@Override
	public String toString() {
		return "EmailTriggered [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", title="
				+ title + ", author=" + author + ", notificationType=" + notificationType + ", eventType=" + eventType
				+ "]";
	} 
    
}
