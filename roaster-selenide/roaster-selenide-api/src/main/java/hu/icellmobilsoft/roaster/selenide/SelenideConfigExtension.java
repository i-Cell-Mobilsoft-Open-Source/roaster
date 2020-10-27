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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.Extension;

import org.apache.commons.lang3.StringUtils;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.selenide.config.SelenideConfig;
import hu.icellmobilsoft.roaster.selenide.drivers.ChromeDeviceModeDriverProvider;
import hu.icellmobilsoft.roaster.selenide.drivers.CustomIEDriverProvider;
import hu.icellmobilsoft.roaster.selenide.enums.Browser;

/**
 * Selenide Configuration extension.
 *
 * @author speter555
 * @since 0.2.0
 */
public class SelenideConfigExtension implements Extension {

    /**
     * Handle container Initialized event
     *
     * @param containerInitialized
     *            instance
     */
    public void observesContainerInitialized(@Observes @Initialized(ApplicationScoped.class) ContainerInitialized containerInitialized) {
        initDriver();
    }

    private void initDriver() {
        Logger logger = Logger.getLogger(SelenideConfigExtension.class);
        SelenideConfig selenideConfig = CDI.current().select(SelenideConfig.class).get();

        logger.debug(">> initDriver()");
        String browserType = selenideConfig.getBrowserType();
        String seleniumRemoteUrl = selenideConfig.getSeleniumUrl();
        String device = selenideConfig.getBrowserDevice();

        if (StringUtils.isNotBlank(device)) {
            ChromeDeviceModeDriverProvider chromeDeviceModeDriverProvider = new ChromeDeviceModeDriverProvider(device, seleniumRemoteUrl);
            Configuration.browser = chromeDeviceModeDriverProvider.getClass().getName();
            Configuration.startMaximized = false;
        } else if (StringUtils.equalsIgnoreCase(Browser.IE.name(), browserType)) {
            CustomIEDriverProvider customIEDriverProvider = new CustomIEDriverProvider(seleniumRemoteUrl);
            Configuration.browser = customIEDriverProvider.getClass().getName();
            Configuration.fastSetValue = true;
            Configuration.startMaximized = true;
        } else {
            Configuration.remote = seleniumRemoteUrl;
            Configuration.browser = browserType;
            Configuration.startMaximized = true;
        }

        Configuration.reportsFolder = "/target";
        Configuration.downloadsFolder = "/target/download";

        Configuration.driverManagerEnabled = StringUtils.isBlank(seleniumRemoteUrl);
        Configuration.headless = selenideConfig.isBrowserHeadless();
        Configuration.timeout = selenideConfig.getTimeout();
        Configuration.baseUrl = selenideConfig.getHomepage();
        WebDriverRunner.clearBrowserCache();
        Selenide.open(Configuration.baseUrl);
        WebDriverRunner.getWebDriver().switchTo().defaultContent();
        logger.debug("<< initDriver()");
    }

}
