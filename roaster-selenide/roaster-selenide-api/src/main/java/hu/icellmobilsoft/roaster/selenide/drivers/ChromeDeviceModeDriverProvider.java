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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.codeborne.selenide.WebDriverProvider;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.roaster.api.TestException;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * WebDriverProvider for Chrome in device mode
 *
 * @author speter555
 * @since 0.2.0
 */
public class ChromeDeviceModeDriverProvider implements WebDriverProvider {

    private Logger logger = Logger.getLogger(ChromeDeviceModeDriverProvider.class);
    private String deviceName;
    private URL remoteUrl;

    public ChromeDeviceModeDriverProvider(String device, String remoteUrlText) throws TestException {
        try {
            if (StringUtils.isNotBlank(remoteUrlText)) {
                remoteUrl = new URL(remoteUrlText);
            }
            deviceName = device;
        } catch (MalformedURLException e) {
            String msg = MessageFormat.format("Cannot create ChromeDeviceModeDriverProvider because remoteUrl ({0}) is malformed Url!",
                    remoteUrlText);
            logger.warn(msg, e);
            throw new TestException(msg, e);
        }
    }

    @Override
    public WebDriver createDriver(final DesiredCapabilities desiredCapabilities) {
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", deviceName);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);

        WebDriver driver;
        if (remoteUrl == null) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(chromeOptions);
        } else {
            driver = new RemoteWebDriver(remoteUrl, chromeOptions);
        }

        return driver;
    }
}
