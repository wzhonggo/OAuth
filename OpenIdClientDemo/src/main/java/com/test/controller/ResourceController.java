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

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

/**
 *
 *
 *
 */
@Controller

public class ResourceController {
    private Logger logger = LoggerFactory.getLogger(TokenController.class);


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


    @RequestMapping("/get_resource")
    public ModelAndView authorize(
            HttpServletRequest req) {

        ModelMap map = new ModelMap();
        try {
            OAuthClientRequest request = null;

//			if (Oauth2Utils.REQUEST_TYPE_QUERY.equals(oauthParams.getRequestType())){
//				request= new OAuthBearerClientRequest(oauthParams.getResourceUrl()).setAccessToken(oauthParams.getAccessToken()).buildQueryMessage();
//			}else if (Oauth2Utils.REQUEST_TYPE_HEADER.equals(oauthParams.getRequestType())){
//				request= new OAuthBearerClientRequest(oauthParams.getResourceUrl()).setAccessToken(oauthParams.getAccessToken()).buildHeaderMessage();
//			}else if (Oauth2Utils.REQUEST_TYPE_BODY.equals(oauthParams.getRequestType())){
//				request= new OAuthBearerClientRequest(oauthParams.getResourceUrl()).setAccessToken(oauthParams.getAccessToken()).buildBodyMessage();
//			}
            request = new OAuthBearerClientRequest("https://www.googleapis.com/plus/v1/people/me/openIdConnect").setAccessToken((String) req.getSession().getAttribute(OAuth.OAUTH_ACCESS_TOKEN)).buildHeaderMessage();

            OAuthClient client = new OAuthClient(new URLConnectionClient());
            OAuthResourceResponse resourceResponse = client.resource(request, "GET", OAuthResourceResponse.class);

            if (resourceResponse.getResponseCode() == 200) {
                logger.info("get_resource" + resourceResponse.getBody());
            } else {
                logger.info("get_resource" + resourceResponse.getBody());
            }
        } catch (OAuthSystemException e) {
            logger.error(e.getMessage());
            map.put("msg", e.getMessage());
        } catch (OAuthProblemException e) {
            logger.error(e.getMessage());
            map.put("msg", e.getMessage());
        }

        return new ModelAndView("index", map);


    }
}
