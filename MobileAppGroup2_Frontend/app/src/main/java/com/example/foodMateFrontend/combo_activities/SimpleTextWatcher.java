package com.example.foodMateFrontend.combo_activities;

import android.text.Editable;
import android.text.TextWatcher;

abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // 默认实现，空方法
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 默认实现，空方法
    }

    @Override
    public abstract void afterTextChanged(Editable s); // 强制子类实现
}