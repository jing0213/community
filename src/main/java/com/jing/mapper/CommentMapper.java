package com.jing.mapper;

import com.jing.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentByEntity(int entityType,int entityId,int offset,int limit);

    int countByEntity(int entityType,int entityId);

    int insertComment(Comment comment);
}