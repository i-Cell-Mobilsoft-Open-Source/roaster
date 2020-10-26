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
import javax.enterprise.inject.spi.Extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.selenide.drivers.ChromeDeviceModeDriverProvider;
import hu.icellmobilsoft.roaster.selenide.drivers.CustomIEDriverProvider;
import hu.icellmobilsoft.roaster.selenide.enums.Browser;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.weld.environment.se.events.ContainerInitialized;

/**
 * Selenide Configuration extension.
 *
 * @author speter555
 * @since 0.2.0
 */
public class SelenideConfigExtension implements Extension {

    private Logger logger = Logger.getLogger(SelenideConfigExtension.class);

    public SelenideConfigExtension() {
    }

    public void observesContainerInitialized(@Observes @Initialized(ApplicationScoped.class) ContainerInitialized containerInitialized) {
        initDriver();
    }

    private void initDriver() {
        logger.debug(">> initDriver()");
        Config config = ConfigProvider.getConfig();
        Boolean deviceMode = config.getOptionalValue("browser.device.mode", Boolean.class).orElse(false);
        String browserType = config.getOptionalValue("browser.type", String.class).orElse(Browser.CHROME.name());
        boolean isRemote = BooleanUtils.isTrue(config.getOptionalValue("selenium.remote", Boolean.class).orElse(false));

        if (BooleanUtils.isTrue(deviceMode) && StringUtils.equalsIgnoreCase(Browser.CHROME.name(), browserType)) {
            ChromeDeviceModeDriverProvider chromeDeviceModeDriverProvider = new ChromeDeviceModeDriverProvider();
            Configuration.browser = chromeDeviceModeDriverProvider.getClass().getName();
            Configuration.startMaximized = false;
        } else if (StringUtils.equalsIgnoreCase(Browser.IE.name(), browserType)) {
            CustomIEDriverProvider customIEDriverProvider = new CustomIEDriverProvider();
            Configuration.browser = customIEDriverProvider.getClass().getName();
            Configuration.fastSetValue = true;
            Configuration.startMaximized = true;
        } else {
            Configuration.remote = isRemote ? config.getOptionalValue("selenium.url", String.class).orElse(StringUtils.EMPTY) : null;
            Configuration.browser = browserType;
            Configuration.startMaximized = true;
        }

        Configuration.driverManagerEnabled = !isRemote;
        Configuration.headless = BooleanUtils.isTrue(config.getOptionalValue("browser.headless", Boolean.class).orElse(true));
        Configuration.timeout = config.getOptionalValue("app.timeout", Long.class).orElse(5000l);
        Configuration.baseUrl = config.getOptionalValue("app.homepage", String.class).orElse(StringUtils.EMPTY);
        WebDriverRunner.clearBrowserCache();
        Selenide.open(Configuration.baseUrl);
        WebDriverRunner.getWebDriver().switchTo().defaultContent();
        logger.debug("<< initDriver()");
    }

}
