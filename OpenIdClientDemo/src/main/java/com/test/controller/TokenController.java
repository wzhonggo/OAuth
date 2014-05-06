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
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.jwt.JWT;
import org.apache.oltu.oauth2.jwt.io.JWTClaimsSetWriter;
import org.apache.oltu.oauth2.jwt.io.JWTHeaderWriter;
import org.apache.oltu.oauth2.jwt.io.JWTWriter;
import org.apache.oltu.openidconnect.client.response.OpenIdConnectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;


/**
 *
 *
 *
 */
@Controller

public class TokenController {

    private Logger logger = LoggerFactory.getLogger(TokenController.class);

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
    @Value("#{openidConnect['tokenEndpoint']}")
    String tokenEndpoint;
    private
    @Value("#{openidConnect['scope']}")
    String scope;
    private
    @Value("#{openidConnect['redirectUri']}")
    String redirectUri;

    private final JWTWriter jwtWriter = new JWTWriter();

    static {
        //for localhost testing only
        HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier() {

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("localhost")) {
                            return true;
                        }
                        return false;
                    }
                });

        trustAllHttpsCertificates();
    }


    private static void trustAllHttpsCertificates() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//
//            public void checkClientTrusted(X509Certificate[] certs, String authType) {
//            }
//
//            public void checkServerTrusted(X509Certificate[] certs, String authType) {
//            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                return new java.security.cert.X509Certificate[0];  //To change body of implemented methods use File | Settings | File Templates.
                return null;
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            ;
        }

    }

    @RequestMapping("/get_token")
    public ModelAndView authorize(
            HttpServletRequest req) throws OAuthSystemException, IOException {
        ModelMap map = new ModelMap();
        try {

            logger.info("==============================get_token=============================================");
            String code = req.getParameter("code");
            logger.info("code=" + code);
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation(tokenEndpoint)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRedirectURI(redirectUri).setScope(scope)
                    .setCode(code)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .buildBodyMessage();

            OAuthClient client = new OAuthClient(new URLConnectionClient());

            OAuthAccessTokenResponse oauthResponse = null;
            Class<? extends OAuthAccessTokenResponse> cl = OpenIdConnectResponse.class;

            oauthResponse = client.accessToken(request, cl);

            logger.info("getAccessToken" + oauthResponse.getAccessToken());
            logger.info("getExpiresIn" + oauthResponse.getExpiresIn());
            logger.info("getRefreshToken" + Oauth2Utils.isIssued(oauthResponse.getRefreshToken()));

            req.getSession().setAttribute(OAuth.OAUTH_ACCESS_TOKEN, oauthResponse.getAccessToken());

            OpenIdConnectResponse openIdConnectResponse = ((OpenIdConnectResponse) oauthResponse);
            JWT idToken = openIdConnectResponse.getIdToken();
            logger.info("idToken" + idToken.getRawString());
            logger.info("getHeader " + idToken.getHeader());
            logger.info("getHeader " + new JWTHeaderWriter().write(idToken.getHeader()));
            logger.info("getClaimsSet " + idToken.getClaimsSet());
            logger.info("getClaimsSet " + new JWTClaimsSetWriter().write(idToken.getClaimsSet()));

            URL url = new URL(tokenEndpoint);

            logger.info("getIdTokenValid  " + openIdConnectResponse.checkId(url.getHost(), clientId));


            return new ModelAndView(new RedirectView("get_resource"));

        } catch (OAuthProblemException e) {
            logger.error(e.getMessage());
            StringBuffer sb = new StringBuffer();
            sb.append("</br>");
            sb.append("Error code: ").append(e.getError()).append("</br>");
            sb.append("Error description: ").append(e.getDescription()).append("</br>");
            sb.append("Error uri: ").append(e.getUri()).append("</br>");
            sb.append("State: ").append(e.getState()).append("</br>");
            map.put("msg", sb.toString());
            return new ModelAndView("index", map);
        }
    }
}
