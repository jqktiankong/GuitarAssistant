package com.example.jqk.guitarassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> lyrics;
    private ArrayList<String> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lyrics = new ArrayList<String>();
        scores = new ArrayList<String>();

        lyrics.add("让我掉下眼泪的 不止昨夜的酒");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("让我依依不舍的 不止你的温柔");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("余路还要走多久 你攥着我的手");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("让我感到为难的 是挣扎的自由");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("分别总是在九月 回忆是思念的愁");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("深秋嫩绿的垂柳 亲吻着我额头");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("在那座阴雨的小城里 我从未忘记你");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("成都 带不走的 只有你");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("和我在成都的街头走一走 喔…");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("直到所有的灯都熄灭了也不停留");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("你会挽着我的衣袖 我会把手揣进裤兜");
        scores.add("   A     B      C  D   E  ");
        lyrics.add("走到玉林路的尽头 坐在小酒馆的门口");
    }
}
