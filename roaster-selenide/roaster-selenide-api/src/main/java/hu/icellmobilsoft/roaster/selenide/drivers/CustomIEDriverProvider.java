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

import com.codeborne.selenide.WebDriverProvider;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Custome Internet Explorer WebDriverProvider
 *
 * @author speter555
 * @since 0.2.0
 */
public class CustomIEDriverProvider implements WebDriverProvider {

    private URL remoteUrl;
    private boolean isLocal;

    public CustomIEDriverProvider() {
        Config config = ConfigProvider.getConfig();
        isLocal = !BooleanUtils.isTrue(config.getValue("selenium.remote", Boolean.class));
        try {
            remoteUrl = new URL(config.getValue("selenium.remote.url", String.class));
        } catch (MalformedURLException e) {
            isLocal = true;
        }
    }

    @Override
    public WebDriver createDriver(final DesiredCapabilities desiredCapabilities) {
        if (isLocal) {
            WebDriverManager.iedriver().setup();
        }

        InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions(desiredCapabilities);
        internetExplorerOptions.addCommandSwitches("-private");
        internetExplorerOptions.withInitialBrowserUrl("about:blank");
        internetExplorerOptions.destructivelyEnsureCleanSession();
        WebDriver driver;
        if (isLocal) {
            driver = new InternetExplorerDriver(internetExplorerOptions);
        } else {
            driver = new RemoteWebDriver(remoteUrl, internetExplorerOptions);
        }

        return (WebDriver) driver;
    }
}
