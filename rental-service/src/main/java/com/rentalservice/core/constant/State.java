package com.rentalservice.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class State {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Fleet {
        public static final String AVAILABLE = "AVAILABLE";
    }
}
