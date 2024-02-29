package com.tobe.healthy.common.message;

import com.tobe.healthy.common.message.model.request.Message;
import com.tobe.healthy.common.message.model.request.MessageIds;
import com.tobe.healthy.common.message.model.request.MessageList;
import com.tobe.healthy.common.message.model.response.AddMessageListModel;
import com.tobe.healthy.common.message.model.response.DeleteGroupModel;
import com.tobe.healthy.common.message.model.response.GetMessageListModel;
import com.tobe.healthy.common.message.model.response.GroupListModel;
import com.tobe.healthy.common.message.model.response.GroupModel;
import com.tobe.healthy.common.message.model.response.MessageModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

// 문서 : https://docs.coolsms.co.kr/rest-api-reference/message-api-v4
// 일부 API는 Query Parameter를 추가로 사용할 수 있습니다.
public interface MsgV4 {
    // 심플 메시지
    @POST("/messages/v4/send")
    Call<MessageModel> sendMessage(@Header("Authorization") String auth,
                                   @Body Message message);
    // 그룹 메시지 - 그룹 생성
    @POST("/messages/v4/groups")
    Call<GroupModel> createGroup(@Header("Authorization") String auth);

    // 그룹 메시지 - 그룹 목록
    @GET("/messages/v4/groups")
    Call<GroupListModel> getGroups(@Header("Authorization") String auth);

    // 그룹 메시지 - 그룹 정보
    @GET("/messages/v4/groups/{groupId}")
    Call<GroupModel> getGroupInfo(@Header("Authorization") String auth,
                                  @Path("groupId") String groupId);

    // 그룹 메시지 - 그룹 삭제
    @DELETE("/messages/v4/groups/{groupId}")
    Call<GroupModel> deleteGroupInfo(@Header("Authorization") String auth,
                                     @Path("groupId") String groupId);

    // 그룹 메시지 - 그룹 메시지 추가
    @PUT("/messages/v4/groups/{groupId}/messages")
    Call<AddMessageListModel> addGroupMessage(@Header("Authorization") String auth,
                                              @Path("groupId") String groupId,
                                              @Body MessageList messages);

    // 그룹 메시지 - 그룹 메시지 발송
    @POST("/messages/v4/groups/{groupId}/send")
    Call<ResponseBody> sendGroupMessage(@Header("Authorization") String auth,
                                        @Path("groupId") String groupId);

    // 그룹 메시지 - 그룹 메시지 삭제
    @HTTP(method = "DELETE", path = "/messages/v4/groups/{groupId}/messages", hasBody = true)
    Call<DeleteGroupModel> deleteGroupMessages(@Header("Authorization") String auth,
                                               @Path("groupId") String groupId,
                                               @Body MessageIds messageIds);

    // 메시지 조회
    @GET("/messages/v4/list")
    Call<GetMessageListModel> getMessageList(@Header("Authorization") String auth);
}
