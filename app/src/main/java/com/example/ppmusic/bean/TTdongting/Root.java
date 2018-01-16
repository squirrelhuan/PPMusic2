package com.example.ppmusic.bean.TTdongting;

import java.util.List;
/**
 * 天天动听接口返回数据
 * @author Administrator
 *
 */
public class Root {
private int code;

private int rows;

private int pages;

private List<Data> data ;

public void setCode(int code){
this.code = code;
}
public int getCode(){
return this.code;
}
public void setRows(int rows){
this.rows = rows;
}
public int getRows(){
return this.rows;
}
public void setPages(int pages){
this.pages = pages;
}
public int getPages(){
return this.pages;
}
public void setData(List<Data> data){
this.data = data;
}
public List<Data> getData(){
return this.data;
}

}