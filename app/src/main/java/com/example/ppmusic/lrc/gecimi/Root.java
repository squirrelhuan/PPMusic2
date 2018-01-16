package com.example.ppmusic.lrc.gecimi;

import java.util.List;
public class Root {
private int count;

private int code;

private List<Result> result ;

public void setCount(int count){
this.count = count;
}
public int getCount(){
return this.count;
}
public void setCode(int code){
this.code = code;
}
public int getCode(){
return this.code;
}
public void setResult(List<Result> result){
this.result = result;
}
public List<Result> getResult(){
return this.result;
}

}