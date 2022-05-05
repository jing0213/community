package com.jing.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.soap.Node;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SensitiveFilter {
    //替换符号
    private static final String REPLACE_ELEMENT = "*";
    //根节点
    private TrieNode rootNode = new TrieNode();
    //初始化
    @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyWord;
            while ((keyWord = reader.readLine()) != null){
                //添加到前缀树
                addKeyword(keyWord);
            }
        } catch (IOException e) {
            log.error("【加载文件】失败"+e.getMessage());
        }

    }

    /**
     * 过滤铭感词 ： 三个指针（1.root  2.比对text的head  3.比对text的tail）
     * @param text 待过滤的文本
     * @return  过滤完成的文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //第一个指针
        TrieNode tmpNode = rootNode;
        //第二三个指针
        int begin = 0,position = 0;
        StringBuilder sb = new StringBuilder();

        while (begin < text.length()){
            if(position < text.length()){
                Character c = text.charAt(position);

                //跳过符号
                if(isSymbol(c)){
                    if(tmpNode == rootNode){
                        begin++;
                        sb.append(c);
                    }
                    position++;
                    continue;
                }
                //检查下级节点
                tmpNode = tmpNode.getSubNode(c);
                if(tmpNode == null){
                    // 以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    // 进入下一个位置
                    position = ++begin;
                    // 重新指向根节点
                    tmpNode = rootNode;

                }else if(tmpNode.isKeywordEnd()){ // 发现敏感词
                    //positon - bengin + 1
                    for(int i = 0 ; i < position - begin + 1; i++){
                        sb.append(REPLACE_ELEMENT);
                    }
                    begin = ++position;
                }else{          // 检查下一个字符
                    position++;
                }
            }// position遍历越界仍未匹配到敏感词
                else{
                    sb.append(text.charAt(begin));
                    position = ++begin;
                    tmpNode = rootNode;
                }
            }
         return sb.toString();
    }

    private boolean isSymbol(Character c) {
        //东南亚字符
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80||c>0x9FFF);
    }


    private void addKeyword(String keyWord) {
        TrieNode tmpNode = rootNode;
        char[] keywords = keyWord.toCharArray();
        for(int i = 0; i < keywords.length; i++){
            TrieNode subNode = tmpNode.getSubNode(keywords[i]);
            if(subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                tmpNode.addSubNode(keywords[i],subNode);
            }
            //改变当前节点
            tmpNode = subNode;

            if(i == keywords.length-1){
                tmpNode.setKeywordEnd(true);
            }
        }
    }



    /**
     * 前缀树节点
     */
    private class TrieNode{
        //关键词结束的标志
        private boolean isKeywordEnd = false;
        //子节点(key:子节点的字符，value:子节点)
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
        //添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c, node);
        }
        //获得子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
