package com.popcloud.dao;

import com.popcloud.model.Message;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;


    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})"})
    int addMessage(Message message);

    @Select({"select", INSERT_FIELDS, ",count(id) as id from ( select * from ", TABLE_NAME,
            "where from_id=#{userId} or to_id=#{userId} order by id desc limit 999) tt group by conversation_id order by id desc limit #{offset},#{limit}"})
    List<Message> getConversationList(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(id) from ", TABLE_NAME, " where has_read = 0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnReadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    @Update({"update", TABLE_NAME, "set has_read=1 where to_id=#{userId} and conversation_id=#{conversationId}"})
    void resetConversationUnReadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    @Select({"select count(id) from ", TABLE_NAME, " where has_read = 0 and to_id=#{userId}"})
    int getTotalMessageCount(@Param("userId") int userId);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by id desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    @Select({" select count(*) from ",TABLE_NAME,"where conversation_id=#{conversationId} "})
    int getConversationCount(@Param("conversationId") String conversationId);

    @Delete({"delete from", TABLE_NAME, "where id=#{messageId}"})
    void deleteMessage(@Param("messageId") Integer messageId);

    @Select({"select conversation_id from", TABLE_NAME, "where id=#{messageId}"})
    String getConversationByMessage(@Param("messageId") Integer messageId);

    @Delete({"delete from", TABLE_NAME, "where conversation_id=#{conversationId}"})
    void deleteConversation(@Param("conversationId") String conversationId);

}
