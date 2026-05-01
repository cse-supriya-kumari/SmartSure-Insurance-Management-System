package com.smartsure.policy.mapper;

import com.smartsure.policy.dto.PolicyDTO;
import com.smartsure.policy.dto.PolicyTypeDTO;
import com.smartsure.policy.dto.PremiumDTO;
import com.smartsure.policy.entity.Policy;
import com.smartsure.policy.entity.PolicyType;
import com.smartsure.policy.entity.Premium;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    @Mapping(target = "policyTypeId", source = "policy.policyType.id")
    @Mapping(target = "policyTypeName", source = "policy.policyType.name")
    @Mapping(target = "status", expression = "java(policy.getStatus().name())")
    @Mapping(target = "premiums", source = "premiums")
    PolicyDTO toPolicyDTO(Policy policy, List<Premium> premiums);

    @Mapping(target = "status", expression = "java(premium.getStatus().name())")
    PremiumDTO toPremiumDTO(Premium premium);

    PolicyTypeDTO toPolicyTypeDTO(PolicyType type);

    List<PolicyTypeDTO> toPolicyTypeDTOList(List<PolicyType> types);
    
    @Named("mapToDTOWithPremiums")
    default PolicyDTO mapToDTOWithPremiums(Policy policy, List<Premium> premiums) {
        return toPolicyDTO(policy, premiums);
    }
}
