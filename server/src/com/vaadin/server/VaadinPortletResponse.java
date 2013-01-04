/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.portlet.MimeResponse;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.http.Cookie;

/**
 * Wrapper for {@link PortletResponse} and its subclasses.
 * 
 * @author Vaadin Ltd.
 * @since 7.0
 * 
 * @see VaadinResponse
 * @see VaadinPortletRequest
 */
public class VaadinPortletResponse implements VaadinResponse {
    static final DateFormat HTTP_DATE_FORMAT = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
    static {
        HTTP_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private final PortletResponse response;
    private VaadinPortletService vaadinService;

    /**
     * Wraps a portlet response and an associated vaadin service
     * 
     * @param response
     *            the portlet response to wrap
     * @param vaadinService
     *            the associated vaadin service
     */
    public VaadinPortletResponse(PortletResponse response,
            VaadinPortletService vaadinService) {
        this.response = response;
        this.vaadinService = vaadinService;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return ((MimeResponse) response).getPortletOutputStream();
    }

    /**
     * Gets the original, unwrapped portlet response.
     * 
     * @return the unwrapped portlet response
     */
    public PortletResponse getPortletResponse() {
        return response;
    }

    @Override
    public void setContentType(String type) {
        ((MimeResponse) response).setContentType(type);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return ((MimeResponse) response).getWriter();
    }

    @Override
    public void setStatus(int responseStatus) {
        response.setProperty(ResourceResponse.HTTP_STATUS_CODE,
                Integer.toString(responseStatus));
    }

    @Override
    public void setHeader(String name, String value) {
        response.setProperty(name, value);
    }

    @Override
    public void setDateHeader(String name, long timestamp) {
        response.setProperty(name, HTTP_DATE_FORMAT.format(new Date(timestamp)));
    }

    @Override
    public void setCacheTime(long milliseconds) {
        VaadinServletResponse.doSetCacheTime(this, milliseconds);
    }

    @Override
    public void sendError(int errorCode, String message) throws IOException {
        setStatus(errorCode);
        getWriter().write(message);
    }

    @Override
    public VaadinPortletService getService() {
        return vaadinService;
    }

    @Override
    public void addCookie(Cookie cookie) {
        response.addProperty(cookie);
    }
}