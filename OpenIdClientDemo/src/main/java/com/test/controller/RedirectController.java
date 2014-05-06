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

package com.test.controller;

import com.test.util.Oauth2Utils;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 *
 *
 */
@Controller
public class RedirectController {

    private Logger logger = LoggerFactory.getLogger(AuthzController.class);

    private
    @Value("#{openidConnect['clientId']}")
    String clientId;
    private
    @Value("#{openidConnect['clientSecret']}")
    String clientSecret;
    private
    @Value("#{openidConnect['authorizationEndpoint']}")
    String authorizationEndpoint;
    private
    @Value("#{openidConnect['scope']}")
    String scope;
    private
    @Value("#{openidConnect['redirectUri']}")
    String redirectUri;

    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
    public ModelAndView handleRedirect(
            HttpServletRequest request,
            HttpServletResponse response) {

        ModelMap map = new ModelMap();
        try {


            // Create the response wrapper
            OAuthAuthzResponse oar = null;
            oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);

            // Get Authorization Code
            String code = oar.getCode();
            logger.info("++++++++++++++code = " + code);
            if (!Oauth2Utils.isEmpty(code)) {
                return new ModelAndView("forward:get_token");
            }
        } catch (OAuthProblemException e) {
            logger.error(e.getMessage());
            StringBuffer sb = new StringBuffer();
            sb.append("</br>");
            sb.append("Error code: ").append(e.getError()).append("</br>");
            sb.append("Error description: ").append(e.getDescription()).append("</br>");
            sb.append("Error uri: ").append(e.getUri()).append("</br>");
            sb.append("State: ").append(e.getState()).append("</br>");

            map.put("msg", sb.toString());
        }
        map.put("msg", "code is null");
        return new ModelAndView("index", map);

    }
}
