package OGTSystem.repository;

import OGTSystem.entity.P2PMessageEntity;
import OGTSystem.mapper.P2PMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class P2PMessageRepository {

    @Autowired
    P2PMessageMapper p2pmessagemapper;

    public List<P2PMessageEntity> getMessagesByMessageFromUUID(String friend){
        return p2pmessagemapper.getMessagesByMessageFromUUID(friend);
    }

    public List<P2PMessageEntity> getMessagesByP2PMessageEntity(P2PMessageEntity p2pmessageentity){
        return p2pmessagemapper.getMessagesByP2PMessageEntity(p2pmessageentity);
    }

    public int createMessage(P2PMessageEntity p2pmessageentity){
        return p2pmessagemapper.createMessage(p2pmessageentity);
    }

    public int setMessage(P2PMessageEntity p2pmessageentity){
        return p2pmessagemapper.setMessage(p2pmessageentity);
    }
}
