package com.jing.mapper;

import com.jing.pojo.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    /**
     * 插入一条凭证
     */
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);
    /**
     * 通过ticket查询这个用户的凭证
     */
    @Select({"select * from login_ticket where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);
    /**
     * 更新凭证的状态
     */
    @Update({"update login_ticket set status = #{status} where ticket = #{ticket}"})
    int updateStatus(String ticket,int status);
}
