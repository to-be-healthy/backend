package com.tobe.healthy.common.message;

import com.tobe.healthy.common.message.model.request.ImageModel;
import com.tobe.healthy.common.message.model.response.DeleteImageResult;
import com.tobe.healthy.common.message.model.response.ImageInfoResult;
import com.tobe.healthy.common.message.model.response.ImageListItem;
import com.tobe.healthy.common.message.model.response.ImageResult;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

// 문서 : https://docs.coolsms.co.kr/rest-api-reference/image-api
public interface ImgApi {
    // 이미지 등록
    @POST("/images/v4/images")
    Call<ImageResult> createImage(@Header("Authorization") String auth,
                                  @Body ImageModel image);

    // 이미지 정보 가져오기
    @GET("/images/v4/images/{imageId}")
    Call<ImageInfoResult> getImageInfo(@Header("Authorization") String auth,
                                       @Path("imageId") String imageId);

    // 이미지 리스트 가져오기
    @GET("/images/v4/images")
    Call<ArrayList<ImageListItem>> getImageList(@Header("Authorization") String auth);

    // 이미지 삭제
    @DELETE("/images/v4/images/{imageId}")
    Call<DeleteImageResult> deleteImageInfo(@Header("Authorization") String auth,
                                       @Path("imageId") String imageId);
}
