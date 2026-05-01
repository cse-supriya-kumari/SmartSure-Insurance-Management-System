package com.smartsure.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;
    private String status;
}