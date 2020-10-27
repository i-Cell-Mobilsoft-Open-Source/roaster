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
package hu.icellmobilsoft.roaster.selenide.drivers;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.codeborne.selenide.WebDriverProvider;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.api.TestException;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Custome Internet Explorer WebDriverProvider
 *
 * @author speter555
 * @since 0.2.0
 */
public class CustomIEDriverProvider implements WebDriverProvider {

    private Logger logger = Logger.getLogger(ChromeDeviceModeDriverProvider.class);
    private URL remoteUrl;

    public CustomIEDriverProvider(String remoteUrlText) throws TestException {
        try {
            if (StringUtils.isNotBlank(remoteUrlText)) {
                remoteUrl = new URL(remoteUrlText);
            }
        } catch (MalformedURLException e) {
            String msg = MessageFormat.format("Cannot create CustomIEDriverProvider because remoteUrl ({0}) is malformed Url!", remoteUrlText);
            logger.warn(msg, e);
            throw new TestException(msg, e);
        }
    }

    @Override
    public WebDriver createDriver(final DesiredCapabilities desiredCapabilities) {
        InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions(desiredCapabilities);
        internetExplorerOptions.addCommandSwitches("-private");
        internetExplorerOptions.withInitialBrowserUrl("about:blank");
        internetExplorerOptions.destructivelyEnsureCleanSession();
        WebDriver driver;
        if (remoteUrl == null) {
            WebDriverManager.iedriver().setup();
            driver = new InternetExplorerDriver(internetExplorerOptions);
        } else {
            driver = new RemoteWebDriver(remoteUrl, internetExplorerOptions);
        }

        return (WebDriver) driver;
    }
}
