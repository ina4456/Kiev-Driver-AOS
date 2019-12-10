package com.kiev.driver.aos.repository.remote.packets;

import com.kiev.driver.aos.repository.remote.packets.server2mdt.CallerInfoResendPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.CancelEmergencyPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.NoticesPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.OrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.OrderInfoProcPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseAckPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseMessageNewPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseMessagePacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseMyInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseNoticeListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponsePeriodSendingPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseRestPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseSMSPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseServiceReportPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseStatisticsDetailPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseStatisticsPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaCancelPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaDecisionPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitAreaOrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallListPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ResponseWaitCallOrderInfoPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ServiceConfigPacket;
import com.kiev.driver.aos.repository.remote.packets.server2mdt.ServiceRequestResultPacket;
import com.kiev.driver.aos.util.LogHelper;


/**
 * Created by zic325 on 2016. 9. 7..
 */
public class ResponsePacket {

	protected int messageType;
	protected byte[] buffers;
	protected int offset;

	public ResponsePacket(byte[] bytes) {
		this.offset = 0;
		parse(bytes);
	}

	public static ResponsePacket create(int messageType, byte[] bytes) {
		switch (messageType) {
			case Packets.RES_ACK:
				return new ResponseAckPacket(bytes);
			case Packets.RES_SERVICE:
				return new ServiceRequestResultPacket(bytes);
			case Packets.RES_NOTICE:
				return new NoticesPacket(bytes);
			case Packets.RES_CONFIG:
				return new ServiceConfigPacket(bytes);
			case Packets.RES_PERIOD:
				return new ResponsePeriodSendingPacket(bytes);
			case Packets.RES_ORDER_INFO_BROADCAST:
				return new OrderInfoPacket(bytes);
			case Packets.RES_ORDER_INFO_PROC:
				return new OrderInfoProcPacket(bytes);
			case Packets.RES_REPORT:
				return new ResponseServiceReportPacket(bytes);
			case Packets.RES_WAIT_AREA_CANCEL:
				return new ResponseWaitAreaCancelPacket(bytes);
			case Packets.RES_WAIT_AREA_ORDER_INFO:
				return new ResponseWaitAreaOrderInfoPacket(bytes);
			case Packets.RES_EMERGENCY_CANCEL:
				return new CancelEmergencyPacket(bytes);
			case Packets.RES_MESSAGE:
				return new ResponseMessagePacket(bytes);
			case Packets.RES_MESSAGE_NEW:
				return new ResponseMessageNewPacket(bytes);
			case Packets.RES_ORDER_INFO:
				return new CallerInfoResendPacket(bytes);
			case Packets.RES_REST:
				return new ResponseRestPacket(bytes);


			/*신규 패킷 추가*/
			case Packets.RES_SEND_SMS:
				return new ResponseSMSPacket(bytes);
			case Packets.RES_MY_INFO:
				return new ResponseMyInfoPacket(bytes);
			case Packets.RES_WAIT_CALL_LIST:
				return new ResponseWaitCallListPacket(bytes);
			case Packets.RES_WAIT_CALL_ORDER:
				return new ResponseWaitCallOrderInfoPacket(bytes);
			case Packets.RES_NOTICE_LIST:
				return new ResponseNoticeListPacket(bytes);
			case Packets.RES_STATISTICS:
				return new ResponseStatisticsPacket(bytes);
			case Packets.RES_STATISTICS_DETAIL:
				return new ResponseStatisticsDetailPacket(bytes);
			case Packets.RES_WAIT_AREA_LIST:
				return new ResponseWaitAreaListPacket(bytes);
			case Packets.RES_WAIT_AREA_DECISION:
				return new ResponseWaitAreaDecisionPacket(bytes);
			default:
				return new ResponsePacket(bytes);
		}
	}

	public void parse(byte[] buffers) {
		this.buffers = buffers;
		messageType = readInt(2);
	}

	public int getMessageType() {
		return messageType;
	}

	// Little Endian
	public int readInt(int length) {
		if (isValid(length)) {
			int ret = 0;
			switch (length) {
				case 1:
					ret = buffers[offset] & 0x000000FF;
					break;
				case 2:
					ret = (buffers[offset + 1] & 0x000000FF) << 8;
					ret += (buffers[offset] & 0x000000FF) << 0;
					break;
				case 3:
					ret = (buffers[offset + 2] & 0x000000FF) << 16;
					ret += (buffers[offset + 1] & 0x000000FF) << 8;
					ret += (buffers[offset] & 0x000000FF) << 0;
					break;
				case 4:
					ret = (buffers[offset + 3] & 0x000000FF) << 24;
					ret += (buffers[offset + 2] & 0x000000FF) << 16;
					ret += (buffers[offset + 1] & 0x000000FF) << 8;
					ret += (buffers[offset] & 0x000000FF) << 0;
					break;
			}

			offset += length;
			return ret;
		} else {
			LogHelper.d(">> ResponsePacket.readInt() ERROR -> buffer:"
					+ ((buffers == null) ? "null" : buffers.length)
					+ " offset:" + offset + " length:" + length);
			return 0;
		}
	}

	public float readFloat(int length) {
		int bits = readInt(length);
		return Float.intBitsToFloat(bits);
	}

	public String readString(int length) {
		if (isValid(length)) {
			String str = "Encoding Fail(EUC-KR)";
			try {
				str = new String(buffers, offset, length, "EUC-KR");
			} catch (Exception e) {
				e.printStackTrace();
			}
			offset += length;

			// byte 중에 쓰레기값이 들어오는 케이스가 있어 예외처리 추가 한다.
			char szGarbage = 0xFFFD;
			str = str.replace('\r', ' ');
			str = str.replace(szGarbage, ' ');

			return str.trim();
		} else {
			LogHelper.d(">> ResponsePacket.readString() ERROR -> buffer:"
					+ ((buffers == null) ? "null" : buffers.length)
					+ " offset:" + offset + " length:" + length);
			return "";
		}
	}

	private boolean isValid(int len) {
		if (buffers == null || offset + len > buffers.length) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String toString() {
		return "ResponsePacket{" +
				"messageType=0x" + Integer.toHexString(messageType) +
				'}';
	}
}
