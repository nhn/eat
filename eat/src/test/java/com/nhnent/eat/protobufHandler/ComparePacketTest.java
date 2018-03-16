package com.nhnent.eat.protobufHandler;

import com.nhnent.eat.handler.ComparePacket;
import org.junit.Test;

public class ComparePacketTest {
    @Test
    public void comparePacket() throws Exception {
        String expect = "{\"unitMessage\":[{\"packetBytes\":{}},{\"packetBytes\":{}},{\"packetBytes\":{}},{\"packetBytes\":{}},{\"packetBytes\":{\"res\":\"SUCCESS\",\"rewardTime\":\"0\",\"rewardText\":\"최대3억쩐!\"}},{\"packetBytes\":{}},{\"packetBytes\":{}},{\"packetBytes\":{}},{\"packetBytes\":{}}]}\n";
        String real = "[{\"unitMessage\":[{\"CooltimeRewardInfoReply\":{\"cooltimeRewardInfo\":{\"res\":\"SUCCESS\",\"rewardTime\":\"0\",\"rewardText\":\"최대3억쩐!\"}}},{\"ShopNewMarkReply\":{\"isShowNewMark\":true}},{\"ServiceInfoReply\":{\"serviceInfo\":{\"infoName\":\"RESTRICTION_USER\",\"info\":\"{\\\"tmDerestrictedTime\\\":0,\\\"nAuthenticationState\\\":0,\\\"IsAdminNotice\\\":false}\"}}},{\"EventInfoReply\":{\"eventInfo\":{\"eventId\":\"EVENT_VIDEO_ADVERTISING\",\"infoType\":\"\",\"info\":\"{\\\"show_new_mark\\\":false}\"}}}]},{\"orderedMessage\":[{\"MainPopupInfoReply\":{\"mainPopupInfo\":{\"mainPopupId\":67,\"buttonText\":\"이벤트참가\",\"imgUrl\":\"http://alpha-images.hangame.co.kr/mobile/msudda/mainpopup/20170817_launchingevent_popup.png\",\"linkType\":6,\"linkValue\":\"http://alpha-eventpark.hangame.com/event/mobilesudda/launching.nhn\",\"todayCheck\":true,\"exposureType\":1,\"priority\":3}}},{\"MainPopupInfoReply\":{\"mainPopupInfo\":{\"mainPopupId\":72,\"buttonText\":\"상품구경\",\"imgUrl\":\"http://alpha-images.hangame.co.kr/mobile/msudda/mainpopup/beach_suda.png\",\"linkType\":2,\"linkValue\":\"ShopScene|gamemoney\",\"todayCheck\":true,\"exposureType\":1,\"priority\":2}}},{\"MainPopupInfoReply\":{\"mainPopupInfo\":{\"mainPopupId\":62,\"buttonText\":\"자세히보기\",\"imgUrl\":\"http://alpha-images.hangame.co.kr/mobile/msudda/mainpopup/ddangevent.png\",\"linkType\":2,\"linkValue\":\"OpenEventScene\",\"todayCheck\":true,\"exposureType\":1,\"priority\":4}}}]}]\n";

        ComparePacket.ComparePacket(expect,real);
    }

}