package com.tobe.healthy.member.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBearerToken is a Querydsl query type for BearerToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBearerToken extends EntityPathBase<BearerToken> {

    private static final long serialVersionUID = 2121463031L;

    public static final QBearerToken bearerToken = new QBearerToken("bearerToken");

    public final StringPath accessToken = createString("accessToken");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final StringPath refreshToken = createString("refreshToken");

    public QBearerToken(String variable) {
        super(BearerToken.class, forVariable(variable));
    }

    public QBearerToken(Path<? extends BearerToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBearerToken(PathMetadata metadata) {
        super(BearerToken.class, metadata);
    }

}

