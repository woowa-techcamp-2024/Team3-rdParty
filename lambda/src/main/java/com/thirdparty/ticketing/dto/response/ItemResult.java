package com.thirdparty.ticketing.dto.response;

import java.util.List;

public record ItemResult<T>(List<T> items) {}
