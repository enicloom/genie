/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.web.controllers;

import com.netflix.genie.test.categories.UnitTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Unit tests for the ControllerUtils class.
 *
 * @author tgianos
 * @since 3.0.0
 */
@Category(UnitTest.class)
public class ControllerUtilsUnitTests {

    /**
     * Test the getRemainingPath method.
     */
    @Test
    public void canGetRemainingPath() {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn(null);
        Assert.assertNull(ControllerUtils.getRemainingPath(request));

        Mockito
            .when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
            .thenReturn("/api/v3/jobs/1234/output/genie/log.out");
        Mockito
            .when(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
            .thenReturn("/api/v3/jobs/{id}/output/**");
        Assert.assertThat(ControllerUtils.getRemainingPath(request), Matchers.is("genie/log.out"));

        Mockito
            .when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
            .thenReturn("/api/v3/jobs/1234/output");
        Mockito
            .when(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
            .thenReturn("/api/v3/jobs/{id}/output");
        Assert.assertThat(ControllerUtils.getRemainingPath(request), Matchers.is(""));

        Mockito
            .when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
            .thenReturn("/api/v3/jobs/1234/output/");
        Mockito
            .when(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
            .thenReturn("/api/v3/jobs/{id}/output/");
        Assert.assertThat(ControllerUtils.getRemainingPath(request), Matchers.is(""));

        Mockito
            .when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
            .thenReturn("/api/v3/jobs/1234/output/stdout");
        Mockito
            .when(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
            .thenReturn("/api/v3/jobs/{id}/output/**");
        Assert.assertThat(ControllerUtils.getRemainingPath(request), Matchers.is("stdout"));
    }

    /**
     * Test {@link ControllerUtils#getRequestRoot(HttpServletRequest, String)}.
     */
    @Test
    public void canGetRequestRoot() {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final String requestURL = "https://genie.com/api/v3/jobs/1234/output/genie/genie.done";
        final StringBuffer buffer = new StringBuffer(requestURL);
        Mockito.when(request.getRequestURL()).thenReturn(buffer);

        Assert.assertThat(ControllerUtils.getRequestRoot(request, ""), Matchers.is(requestURL));
        Assert.assertThat(
            ControllerUtils.getRequestRoot(request, ".done"),
            Matchers.is("https://genie.com/api/v3/jobs/1234/output/genie/genie")
        );
        Assert.assertThat(
            ControllerUtils.getRequestRoot(request, "genie/genie.done"),
            Matchers.is("https://genie.com/api/v3/jobs/1234/output/")
        );
    }
}
