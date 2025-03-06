package org.savand.model.dto;

import lombok.Builder;

@Builder
public record FormDto(String userName, String phoneNumber) {
}
