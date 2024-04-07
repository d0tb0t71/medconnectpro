package com.example.medconnectpro;

public interface OnItemClick {
    <T> void onClick (T model);

    void onClickDelete(String s);
}
