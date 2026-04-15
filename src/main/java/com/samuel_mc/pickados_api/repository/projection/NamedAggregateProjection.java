package com.samuel_mc.pickados_api.repository.projection;

public interface NamedAggregateProjection {
    String getLabel();
    long getTotal();
}
