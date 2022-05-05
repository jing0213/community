package com.jing.service;

import com.jing.mapper.DiscussPostMapper;
import com.jing.pojo.DiscussPost;
import com.jing.utils.SensitiveFilter;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return  discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不可为空！");
        }
        //转义Html标记 比如发标签《script》就不行了
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤铭感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return discussPostMapper.insertDisscussPost(post);
    }
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPost(id);
    }
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
}
