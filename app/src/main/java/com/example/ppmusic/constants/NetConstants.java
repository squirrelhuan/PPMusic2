package com.example.ppmusic.constants;

/**
 * 
 * 进行网络请求的url常量
 * 
 * @author tjacer 接口：http://api.erzao.org/portal/face
 */
public class NetConstants {
	
	 public static final String SERVER_URL = "http://115.47.6.106:809";
	 //天天动听歌曲api
	 public static final String SERVER_TianTian_URL = "http://search.dongting.com/song/search/old";
     //歌词迷api
	 public static final String SERVER_GECIMI_LRC_URL = "http://gecimi.com/api/lyric/";
//		歌曲搜索API：http://search.kuwo.cn/r.s?all={0}&ft=music& itemset=web_2013&client=kt&pn={1}&rn={2}&rformat=json&encoding=utf8
//		{0}=需要搜索的歌曲或歌手
//		{1}=查询的页码数
//		{2}=当前页的返回数量
	 public static final String SERVER_KUWO_MUSIC_URL = "http://search.kuwo.cn/r.s";
	 //http://artistpicserver.kuwo.cn/pic.web?type=rid_pic&pictype=url&size=[图片大小,一般为70]&rid=[歌曲id]
	 public static final String SERVER_KUWO_IMAGE_HEAD_URL = "http://artistpicserver.kuwo.cn/pic.web";
	 //http://artistpicserver.kuwo.cn/pic.web?type=big_artist_pic&pictype=url&content=list&&id=0&name=[歌手名]&rid=[可为空]from=pc&json=[json数据排放方式,一般为1]&version=1&width=[写真宽度]&height=[写真高度]
	 public static final String SERVER_KUWO_IMAGE_SINGER_URL = "http://artistpicserver.kuwo.cn/pic.web";
}