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
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles requests for the application welcome page.
 */
@Controller
public class AuthzController {


    private Logger logger = LoggerFactory.getLogger(AuthzController.class);


    private @Value("#{openidConnect['clientId']}") String clientId;
    private @Value("#{openidConnect['clientSecret']}") String clientSecret;
    private @Value("#{openidConnect['authorizationEndpoint']}") String authorizationEndpoint;
    private @Value("#{openidConnect['scope']}") String scope;
    private @Value("#{openidConnect['redirectUri']}") String redirectUri;


    @RequestMapping("/authorize")
    public ModelAndView authorize(
                                  HttpServletRequest req,
                                  HttpServletResponse res)
        throws OAuthSystemException, IOException {

            OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(authorizationEndpoint)
                .setClientId(clientId)
                .setRedirectURI(redirectUri)
                .setResponseType(ResponseType.CODE.toString())
                .setScope(scope)
                .setState(Oauth2Utils.getUuidString())
                .buildQueryMessage();

            return new ModelAndView(new RedirectView(request.getLocationUri()));
    }

}
