package com.jing.controller;

import com.jing.pojo.Comment;
import com.jing.pojo.DiscussPost;
import com.jing.pojo.Page;
import com.jing.pojo.User;
import com.jing.service.CommentService;
import com.jing.service.DiscussPostService;
import com.jing.service.UserService;
import com.jing.utils.CommunityConstant;
import com.jing.utils.CommunityUtils;
import com.jing.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtils.getJSONString(403,"你话没有登录！");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        return CommunityUtils.getJSONString(0,"发布成功！");
    }
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId")int discussPostId, Model model, Page page){
        //帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        //作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("post",post);
        model.addAttribute("user",user);
        //comment
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());
        model.addAttribute("page",page);
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPostId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> commentVoObject = new ArrayList<>();
        if(commentList !=null){
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment", comment);
                //用户
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //评论的回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String,Object>> replyVoObject = new ArrayList<>();
                if(replyList !=null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        //评论
                        replyVo.put("reply", reply);
                        //用户
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        //目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getUserId());
                        replyVo.put("target", target);
                        replyVoObject.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoObject);
                //回复数量
                int replyNums = commentService.countByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyNums",replyNums);
                commentVoObject.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoObject);
        return "/site/discuss-detail";
    }
}
