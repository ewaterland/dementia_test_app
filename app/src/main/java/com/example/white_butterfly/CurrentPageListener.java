package com.example.white_butterfly;

// 프래그먼트로 값을 전달하는 용도
public interface CurrentPageListener {
    void onPageUpdated(int currentPage);
    int getCurrentPage();
}

