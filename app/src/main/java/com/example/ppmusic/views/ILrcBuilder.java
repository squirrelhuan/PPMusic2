package com.example.ppmusic.views;


import java.util.List;

import com.example.ppmusic.view.impl.LrcRow;

/**
 * 解析歌词，得到LrcRow的集合
 */
public interface ILrcBuilder {
    List<LrcRow> getLrcRows(String rawLrc);
}
