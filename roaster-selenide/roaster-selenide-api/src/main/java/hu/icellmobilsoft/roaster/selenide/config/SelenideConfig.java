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
package hu.icellmobilsoft.roaster.selenide.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Config helper class with logging.
 * 
 * @author speter555
 * @since 0.2.0
 */
@ApplicationScoped
public class SelenideConfig {

    private static final String DOT = ".";
    private static final String SELENIDE_ROOT = "roaster.selenide";
    private static final String HOMEPAGE = "homepage";
    private static final String TIMEOUT = "timeout";
    private static final String SELENIUM = "selenium";
    private static final String URL = "url";
    private static final String BROWSER = "browser";
    private static final String TYPE = "type";
    private static final String HEADLESS = "headless";
    private static final String DEVICE = "device";
    private static final String DECISION = "decision";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";

    private static final String SELENIDE_HOMEPAGE = SELENIDE_ROOT + DOT + HOMEPAGE;
    private static final String SELENIDE_TIMEOUT = SELENIDE_ROOT + DOT + TIMEOUT;
    private static final String SELENIDE_SELENIUM_REMOTE = SELENIDE_ROOT + DOT + SELENIUM + DOT + URL;
    private static final String SELENIDE_BROWSER_TYPE = SELENIDE_ROOT + DOT + BROWSER + DOT + TYPE;
    private static final String SELENIDE_BROWSER_HEADLESS = SELENIDE_ROOT + DOT + BROWSER + DOT + HEADLESS;
    private static final String SELENIDE_BROWSER_DEVICE = SELENIDE_ROOT + DOT + BROWSER + DOT + DEVICE;
    private static final String SELENIDE_BROWSER_DECISION_WIDTH = SELENIDE_ROOT + DOT + BROWSER + DOT + DECISION + DOT + WIDTH;
    private static final String SELENIDE_BROWSER_DECISION_HEIGHT = SELENIDE_ROOT + DOT + BROWSER + DOT + DECISION + DOT + HEIGHT;

    @Inject
    private Config config;

    private Logger logger = Logger.getLogger(SelenideConfig.class);

    /**
     * Selenide start homepage config, with log
     * 
     * @return homepage url, if it is not find, return empty string
     */
    public String getHomepage() {
        String homepage = config.getOptionalValue(SELENIDE_HOMEPAGE, String.class).orElse(StringUtils.EMPTY);
        logger.info("{0} : [{1}]", SELENIDE_HOMEPAGE, homepage);
        return homepage;
    }

    /**
     * Selenide timeout in milliseconds config, with log
     * 
     * @return selenium used timeout , if it is not find, return 5000
     */
    public Integer getTimeout() {
        Integer timeout = config.getOptionalValue(SELENIDE_TIMEOUT, Integer.class).orElse(5000);
        logger.info("{0} : [{1}]", SELENIDE_TIMEOUT, timeout);
        return timeout;
    }

    /**
     * Selenium remote url config, with log
     * 
     * @return selenium remote url , if it is not find, return null
     */
    public String getSeleniumUrl() {
        String seleniumRemote = config.getOptionalValue(SELENIDE_SELENIUM_REMOTE, String.class).orElse(null);
        logger.info("{0} : [{1}]", SELENIDE_SELENIUM_REMOTE, seleniumRemote);
        return seleniumRemote;
    }

    /**
     * Browser type config, with log
     * 
     * @return browser type string, if it is not find, return empty string
     */
    public String getBrowserType() {
        String browserType = config.getOptionalValue(SELENIDE_BROWSER_TYPE, String.class).orElse(StringUtils.EMPTY);
        logger.info("{0} : [{1}]", SELENIDE_BROWSER_TYPE, browserType);
        return browserType.toLowerCase();
    }

    /**
     * Headless config, with log
     * 
     * @return true if headless config is true, otherwise return false
     */
    public boolean isBrowserHeadless() {
        boolean browserHeadless = BooleanUtils.isTrue(config.getOptionalValue(SELENIDE_BROWSER_HEADLESS, Boolean.class).orElse(Boolean.FALSE));
        logger.info("{0} : [{1}]", SELENIDE_BROWSER_HEADLESS, browserHeadless);
        return browserHeadless;
    }

    /**
     * Device name config for emulation, with log
     * 
     * @return device config, if it is not find, return empty string
     */
    public String getBrowserDevice() {
        String browserDevice = config.getOptionalValue(SELENIDE_BROWSER_DEVICE, String.class).orElse(StringUtils.EMPTY);
        logger.info("{0} : [{1}]", SELENIDE_BROWSER_DEVICE, browserDevice);
        return browserDevice;
    }

    /**
     * Device name config for emulation, with log
     *
     * @return device config, if it is not find, return empty string
     */
    public Integer getBrowserDecisionWidth() {
        Integer browserDecisionWidth = config.getOptionalValue(SELENIDE_BROWSER_DECISION_WIDTH, Integer.class).orElse(null);
        logger.info("{0} : [{1}]", SELENIDE_BROWSER_DECISION_WIDTH, browserDecisionWidth);
        return browserDecisionWidth;
    }

    /**
     * Device name config for emulation, with log
     *
     * @return device config, if it is not find, return empty string
     */
    public Integer getBrowserDecisionHeight() {
        Integer browserDecisionHeight = config.getOptionalValue(SELENIDE_BROWSER_DECISION_HEIGHT, Integer.class).orElse(null);
        logger.info("{0} : [{1}]", SELENIDE_BROWSER_DECISION_HEIGHT, browserDecisionHeight);
        return browserDecisionHeight;
    }
}
