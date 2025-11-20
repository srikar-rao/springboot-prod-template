package com.dev.org.common.response;

import lombok.Builder;

@Builder
public record ResponseStatus(boolean isSuccess, String message, String detailMessage) {}
