package com.example.tennisbooking.TextWatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class DateTimeTextWatcher implements TextWatcher {

    private EditText editText;
    private String current = "";
    private String format = "yyyyMMddTHHmmss"; // 用于控制输入顺序
    private boolean isDeleting = false; // 检测用户是否在删除字符

    public DateTimeTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        isDeleting = count > after; // 如果 count 大于 after，说明是在删除字符
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // 不需要实现
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();

        // 防止递归
        if (text.equals(current)) {
            return;
        }

        // 清除所有的非数字字符和'T'
        String cleanText = text.replaceAll("[^\\d]", "");

        // 如果用户正在删除字符，允许删除时跳过自动格式化
        if (isDeleting) {
            current = cleanText;
            editText.removeTextChangedListener(this);
            editText.setText(current);
            editText.setSelection(current.length());
            editText.addTextChangedListener(this);
            return;
        }

        // 格式化字符串，确保符合 "yyyy-MM-dd'T'HH:mm:ss" 的格式
        StringBuilder formattedText = new StringBuilder();

        int length = cleanText.length();
        for (int i = 0; i < length && i < format.length(); i++) {
            formattedText.append(cleanText.charAt(i));

            if (i == 3 || i == 5) {
                formattedText.append("-"); // 添加日期中的"-"
            } else if (i == 7) {
                formattedText.append("T"); // 添加日期和时间的'T'
            } else if (i == 9 || i == 11) {
                formattedText.append(":"); // 添加时间中的":"
            }
        }

        // 设置当前文本并更新编辑框
        current = formattedText.toString();
        editText.removeTextChangedListener(this);
        editText.setText(current);
        editText.setSelection(current.length());
        editText.addTextChangedListener(this);
    }
}
