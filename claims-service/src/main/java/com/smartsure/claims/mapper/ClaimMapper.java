package com.smartsure.claims.mapper;

import com.smartsure.claims.dto.ClaimDTO;
import com.smartsure.claims.dto.ClaimDocumentDTO;
import com.smartsure.claims.entity.Claim;
import com.smartsure.claims.entity.ClaimDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

    @Mapping(target = "status", expression = "java(claim.getStatus().name())")
    @Mapping(target = "documents", source = "documents")
    ClaimDTO toClaimDTO(Claim claim, List<ClaimDocument> documents);

    ClaimDocumentDTO toClaimDocumentDTO(ClaimDocument document);

    List<ClaimDocumentDTO> toClaimDocumentDTOList(List<ClaimDocument> documents);
}
