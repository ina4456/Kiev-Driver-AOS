package com.kiev.driver.aos.repository.remote;


import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

import androidx.annotation.NonNull;

public interface SocketRequestCallback {
    void onResponse(@NonNull ResponsePacket response);
    void onError(String msg);
}
