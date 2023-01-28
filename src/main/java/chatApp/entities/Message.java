package chatApp.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import static chatApp.utilities.Utility.*;
import static chatApp.utilities.messages.LoggerMessages.createPrivateRoomMessage;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String sender;
    @Column(nullable = false)
    private String receiver;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private String roomId;
    @Column(nullable = false, name = "issue_date")
    private LocalDateTime issueDate;
    @Column(nullable = false, name = "issue_date_time")
    private long issueDateEpoch;


    Message() {
    }

    public Message(String sender, String content, String receiver, String roomId) {
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
        this.receiver = receiver;
        this.issueDate = getLocalDateTimeNow();
        this.issueDateEpoch = this.issueDate.toEpochSecond(ZoneOffset.of(zoneOffsetId));
    }
    public static Message PrivateChatMessageFactory(Message message) {
        Message m = new Message();
        m.setSender(message.getSender());
        m.setContent(message.getContent());
        m.setRoomId(message.getRoomId());
        m.setReceiver(message.getReceiver());
        m.setIssueDate(getLocalDateTimeNow());
        m.setIssueDateEpoch(m.getIssueDate().toEpochSecond(ZoneOffset.of(zoneOffsetId)));
       return m;
    }
    public static Message MainChatMessageFactory(Message message) {
        Message m = new Message();
        m.setSender(message.getSender());
        m.setContent(message.getContent());
        m.setRoomId(message.getRoomId());
        m.setReceiver(mainRoomReceiverName);
        m.setIssueDate(getLocalDateTimeNow());
        m.setIssueDateEpoch(m.getIssueDate().toEpochSecond(ZoneOffset.of(zoneOffsetId)));
        return m;
    }
    public static Message createFirstPrivateRoomMessageFactory(String senderUser, String receiverUser, Long senderId ,Long receiverId) {
        Message m = new Message();
        m.setSender(senderUser);
        m.setContent(firstPrivateMessage);
        m.setRoomId(senderId + separator + receiverId);
        m.setReceiver(receiverUser);
        m.setIssueDate(getLocalDateTimeNow());
        m.setIssueDateEpoch(m.getIssueDate().toEpochSecond(ZoneOffset.of(zoneOffsetId)));
        return m;
    }
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public long getIssueDateEpoch() {
        return issueDateEpoch;
    }

    public void setIssueDateEpoch(long issueDateEpoch) {
        this.issueDateEpoch = issueDateEpoch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (issueDateEpoch != message.issueDateEpoch) return false;
        if (!Objects.equals(id, message.id)) return false;
        if (!sender.equals(message.sender)) return false;
        if (!receiver.equals(message.receiver)) return false;
        if (!content.equals(message.content)) return false;
        if (!roomId.equals(message.roomId)) return false;
        return issueDate.equals(message.issueDate);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + sender.hashCode();
        result = 31 * result + receiver.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + roomId.hashCode();
        result = 31 * result + issueDate.hashCode();
        result = 31 * result + (int) (issueDateEpoch ^ (issueDateEpoch >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                ", roomId='" + roomId + '\'' +
                ", issueDate=" + issueDate +
                ", issueDateEpoch=" + issueDateEpoch +
                '}';
    }
}