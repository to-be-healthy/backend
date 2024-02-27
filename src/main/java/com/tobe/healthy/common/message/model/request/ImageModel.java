package com.tobe.healthy.common.message.model.request;

public class ImageModel {
    public ImageModel(String base64encoded) {
        image = base64encoded;
    }

    String image;
}
