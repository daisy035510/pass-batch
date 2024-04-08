package com.fastcampus.pass.repository.pass;

import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-03T17:19:14+0900",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 18.0.2 (Amazon.com Inc.)"
)
public class PassModelMapperImpl implements PassModelMapper {

    @Override
    public PassEntity toPassEntity(BulkPassEntity bulkPassEntity, String userId) {
        if ( bulkPassEntity == null && userId == null ) {
            return null;
        }

        PassEntity passEntity = new PassEntity();

        if ( bulkPassEntity != null ) {
            passEntity.setStatus( status( bulkPassEntity.getStatus() ) );
            passEntity.setRemainingCount( bulkPassEntity.getCount() );
            passEntity.setPackageSeq( bulkPassEntity.getPackageSeq() );
            passEntity.setStartedAt( bulkPassEntity.getStartedAt() );
            passEntity.setEndedAt( bulkPassEntity.getEndedAt() );
        }
        passEntity.setUserId( userId );

        return passEntity;
    }
}
