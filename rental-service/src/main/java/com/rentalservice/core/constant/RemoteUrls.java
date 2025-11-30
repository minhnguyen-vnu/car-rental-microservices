package com.rentalservice.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoteUrls {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Fleet {
        public static final String GET_VEHICLE_DETAILS = "api/vehicle/get";
        public static final String GET_VEHICLE_BLOCKS = "api/vehicle-blocks/get";
    }
}
