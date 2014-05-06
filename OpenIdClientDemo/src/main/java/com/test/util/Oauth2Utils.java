/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.util;

import org.apache.oltu.oauth2.common.OAuthProviderType;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 *
 *
 *
 */
public final class Oauth2Utils {
    private Oauth2Utils() {
    }

    
    public static final String REQUEST_TYPE_QUERY= "queryParameter";
    public static final String REQUEST_TYPE_HEADER= "headerField";
    public static final String REQUEST_TYPE_BODY= "bodyParameter";



    public static boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }


//    public static String findCookieValue(HttpServletRequest request, String key) {
//        Cookie[] cookies = request.getCookies();
//
//        for (Cookie cookie : cookies) {
//            if (cookie.getName().equals(key)) {
//                return cookie.getValue();
//            }
//        }
//        return "";
//    }

    public static String isIssued(String value) {
        if (isEmpty(value)) {
            return "(Not issued)";
        }
        return value;
    }

    public static String getUuidString() {
        return UUID.randomUUID().toString();
    }
}
