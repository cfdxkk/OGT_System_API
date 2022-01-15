package OGTSystem.mapper;

import OGTSystem.entity.P2PMessageEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface P2PMessageMapper {
    List<P2PMessageEntity> getMessagesByMessageFromUUID (String messageFrom);
    List<P2PMessageEntity> getMessagesByP2PMessageEntity (P2PMessageEntity p2pmessageentity);
    int createMessage(P2PMessageEntity p2pmessageentity);
    int setMessage(P2PMessageEntity p2pmessageentity);

}
