/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsoft.roaster.selenide;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.openqa.selenium.chrome.ChromeOptions;

import com.codeborne.selenide.Browsers;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.api.TestException;
import hu.icellmobilsoft.roaster.selenide.config.SelenideConfig;

/**
 * Selenide Configuration extension.
 *
 * @author speter555
 * @since 0.2.0
 */
@ApplicationScoped
public class SelenideConfiguration {

    @Inject
    private SelenideConfig selenideConfig;

    private Logger logger = Logger.getLogger(SelenideConfiguration.class);

    /**
     * Handle container Initialized event
     *
     * @param containerInitialized
     *            instance
     */
    public void observesContainerInitialized(@Observes ContainerInitialized containerInitialized) {
        initDriver();
    }

    private void initDriver() {

        logger.debug(">> initDriver()");
        String browserType = selenideConfig.getBrowserType();

        if (StringUtils.equalsAnyIgnoreCase(browserType, Browsers.IE, Browsers.INTERNET_EXPLORER)) {
            throw new TestException(MessageFormat.format("{0} not supported!", browserType));
        }

        String seleniumRemoteUrl = selenideConfig.getSeleniumUrl();
        String device = selenideConfig.getBrowserDevice();
        Integer decisionWidth = selenideConfig.getBrowserDecisionWidth();
        Integer decisionHeight = selenideConfig.getBrowserDecisionHeight();

        if (StringUtils.isNotBlank(device)) {
            Configuration.browser = Browsers.CHROME;

            Map<String, String> mobileEmulation = new HashMap<>();
            mobileEmulation.put("deviceName", device);
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
            Configuration.browserCapabilities = chromeOptions;
        } else {
            Configuration.browser = browserType;
            if (decisionWidth != null && decisionHeight != null) {
                Configuration.browserSize = MessageFormat.format("{0}x{1}", String.valueOf(decisionWidth), String.valueOf(decisionHeight));
            }
        }

        Configuration.remote = seleniumRemoteUrl;
        Configuration.headless = selenideConfig.isBrowserHeadless();
        Configuration.timeout = selenideConfig.getTimeout();
        Configuration.baseUrl = selenideConfig.getHomepage();
        Selenide.open(Configuration.baseUrl);
        Selenide.clearBrowserLocalStorage();
        Selenide.clearBrowserCookies();
        logger.debug("<< initDriver()");
    }

}
