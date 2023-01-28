package chatApp.repository;

import chatApp.entities.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomId(String roomId);

    List<Message> findByRoomIdAndIssueDateEpochBetween(String roomId, long to, long from);

    List<Message> findByRoomId(String roomId, Pageable pageable);

    List<Message> findByContent(String content);

    List<Message> findBySender(String sender);

    List<Message> findBySenderAndContent(String content, String Sender);

    List<Message> findByReceiver(String receiver);
}

