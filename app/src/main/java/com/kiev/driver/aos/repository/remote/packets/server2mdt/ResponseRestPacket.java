package com.kiev.driver.aos.repository.remote.packets.server2mdt;

import com.kiev.driver.aos.repository.remote.packets.Packets;
import com.kiev.driver.aos.repository.remote.packets.ResponsePacket;

/**
 * 휴식/운행응답 (GT-5B12) 5 Byte
 * Server -> MDT
 */
public class ResponseRestPacket extends ResponsePacket {

    private int carId; // Car ID (4)
    private Packets.RestType restType; // 휴식 상태 (1) (규격서의 Description이 잘못 되었음)

    public ResponseRestPacket(byte[] bytes) {
        super(bytes);
    }

    public int getCarId() {
        return carId;
    }

    public Packets.RestType getRestType() {
        return restType;
    }

    @Override
    public void parse(byte[] buffers) {
        super.parse(buffers);
        carId = readInt(4);
        int type = readInt(1);
        if (Packets.RestType.Rest.value == type) {
            restType = Packets.RestType.Rest;
        } else if (Packets.RestType.Working.value == type) {
            restType = Packets.RestType.Working;
        } else if (Packets.RestType.Vacancy.value == type) {
            restType = Packets.RestType.Vacancy;
        } else if (Packets.RestType.KumHo.value == type) {
            restType = Packets.RestType.KumHo;
        } else if (Packets.RestType.Hankook.value == type) {
            restType = Packets.RestType.Hankook;
        } else if (Packets.RestType.Kwangshin.value == type) {
            restType = Packets.RestType.Kwangshin;
        } else if (Packets.RestType.VacancyError.value == type) {
            restType = Packets.RestType.VacancyError;
        } else if (Packets.RestType.TachoMeterError.value == type) {
            restType = Packets.RestType.TachoMeterError;
        } else if (Packets.RestType.ModemError.value == type) {
            restType = Packets.RestType.ModemError;
        }
    }

    @Override
    public String toString() {
        return "휴식/운행응답 (0x" + Integer.toHexString(messageType) + ") " +
                "carId=" + carId +
                ", restType=" + restType;
    }
}