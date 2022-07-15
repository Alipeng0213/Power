/*
 * Copyright (c) 2018-2019 yingtingxu(徐应庭). All rights reserved.
 */

package com.family.auth.core;

import java.lang.annotation.*;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiResultDisabled {
}