/*-
 * #%L
 * Roaster
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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.openqa.selenium.chrome.ChromeOptions;

import com.codeborne.selenide.Browsers;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
     * Default constructor, constructs a new object.
     */
    public SelenideConfiguration() {
        super();
    }

    /**
     * Handle container Initialized event
     *
     * @param containerInitialized instance
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
            addClipboardPrefsToChromeOptions(chromeOptions);
            Configuration.browserCapabilities = chromeOptions;
        } else {
            Configuration.browser = browserType;
            if (decisionWidth != null && decisionHeight != null) {
                Configuration.browserSize = MessageFormat.format("{0}x{1}", String.valueOf(decisionWidth), String.valueOf(decisionHeight));
            }

            if (browserType.equals(Browsers.CHROME)) {
                ChromeOptions chromeOptions = new ChromeOptions();
                addClipboardPrefsToChromeOptions(chromeOptions);
                Configuration.browserCapabilities = chromeOptions;
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

    private void addClipboardPrefsToChromeOptions(ChromeOptions options) {
        Map<String, Object> prefs = new HashMap<String, Object>();
        //0 is default , 1 is enable and 2 is disable
        prefs.put("profile.content_settings.exceptions.clipboard", getClipBoardSettingsMap(1));
        options.setExperimentalOption("prefs", prefs);
    }

    private Map<String, Object> getClipBoardSettingsMap(int settingValue) {
        Map<String, Object> map = new HashMap<>();
        map.put("last_modified", String.valueOf(System.currentTimeMillis()));
        map.put("setting", settingValue);
        Map<String, Object> cbPreference = new HashMap<>();
        cbPreference.put("[*.],*", map);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(cbPreference);
        } catch (JsonProcessingException e) {
            logger.error("Json processing exception. " + e);
        }
        logger.info("clipboardSettingJson: " + json);
        return cbPreference;
    }
}
