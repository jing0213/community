package com.jing.pojo;

public class Page {
    //当前页码
    private int current = 1;
    //显示上限
    private int limit = 10;
    //数据总数
    private int rows;
    //查询路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1)
            this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >=1 &&limit<=100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0)
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页面的起始数据行
     * @return
     */
    public int getOffset(){
        return (current-1)*limit;
    }

    /**
     * 获取总页数
     */
    public int getTotal(){
        if(rows%limit == 0)
            return  rows/limit;
        else
            return rows/limit+1;
    }

    /**
     * 获取要显示的起始页码比如1-100
     * @return
     */
    public int getFrom(){
        int from = current -2;
        from = from < 0?1:from;
        return from;
    }
    public int getTo(){
        int to = current +2;
        to = to > getTotal()?getTotal():to;
        return to;
    }
}
