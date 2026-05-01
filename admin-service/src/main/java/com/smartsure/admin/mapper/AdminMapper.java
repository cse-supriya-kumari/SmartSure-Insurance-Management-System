package com.smartsure.admin.mapper;

import com.smartsure.admin.dto.DashboardReportDTO;
import com.smartsure.admin.dto.ReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(target = "status", constant = "Dashboard generated successfully")
    DashboardReportDTO toDashboardReportDTO(ReportDTO report);
}
