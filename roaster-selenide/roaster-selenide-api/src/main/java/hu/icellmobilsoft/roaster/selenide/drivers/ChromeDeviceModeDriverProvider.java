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
import java.util.HashMap;
import java.util.Map;

import com.codeborne.selenide.WebDriverProvider;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * WebDriverProvider for Chrome in device mode
 *
 * @author speter555
 * @since 0.2.0
 */
public class ChromeDeviceModeDriverProvider implements WebDriverProvider {

    private String deviceName;
    private URL remoteUrl;
    private boolean isLocal;

    public ChromeDeviceModeDriverProvider() {
        Config config = ConfigProvider.getConfig();
        deviceName = config.getValue("browser.device.name", String.class);
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
            WebDriverManager.chromedriver().setup();
        }

        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", deviceName);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        WebDriver driver;
        if (isLocal) {
            driver = new ChromeDriver(chromeOptions);
        } else {
            driver = new RemoteWebDriver(remoteUrl, chromeOptions);
        }

        return driver;
    }
}
