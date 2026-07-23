package com.bicosteve.api_gateway.cache;

import com.bicosteve.api_gateway.models.Event;

import java.util.List;

public record CachePage(List<Event> events, boolean hasNext) { }
