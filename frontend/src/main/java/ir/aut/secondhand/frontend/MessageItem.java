package ir.aut.secondhand.frontend;

import java.time.LocalDateTime;

public class MessageItem {

    private final String text;
    private final boolean sentByMe;
    private final LocalDateTime dateTime;
    private final boolean seen;

    public MessageItem(String text, boolean sentByMe, LocalDateTime dateTime, boolean seen){
        this.text = text;
        this.sentByMe = sentByMe;
        this.dateTime = dateTime;
        this.seen = seen;
    }

    public String getText() {
        return text;
    }

    public boolean isSentByMe() {
        return sentByMe;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isSeen() {
        return seen;
    }
}
