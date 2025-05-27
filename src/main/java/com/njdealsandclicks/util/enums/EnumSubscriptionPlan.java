package com.njdealsandclicks.util.enums;

import java.util.Arrays;

public enum EnumSubscriptionPlan {
    FREE("Free"),
    PREMIUM("Premium"),
    PRO("Pro");

    private final String name;

    EnumSubscriptionPlan(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // public static boolean isSupported(String name) {
    //     return from(name).isPresent();
    // }
    
    // public static Optional<EnumSubscriptionPlan> from(String name) {
    //     return Arrays.stream(values())
    //         .filter(p -> p.name().equalsIgnoreCase(name))
    //         .findFirst();
    // }
    
    public static EnumSubscriptionPlan fromName(String name) {
        return Arrays.stream(EnumSubscriptionPlan.values())
                     .filter(plan -> plan.name.equalsIgnoreCase(name))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid subscription plan: " + name));
    }
}
