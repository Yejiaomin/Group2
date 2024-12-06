package com.example.foodMateFrontend;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodMateFrontend.combo_activities.ComboListActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // 假设您有一个错误监听或来自其他 Activity 的广播，可以根据需要实现。
    }

    // 调用此方法以处理错误并退出加载界面
    public void handleError() {
        Intent intent = new Intent(this, ComboListActivity.class);
        intent.putExtra("error_message", "Failed to generate combos. Please try again.");
        startActivity(intent);
        finish(); // 关闭当前活动
    }
}
