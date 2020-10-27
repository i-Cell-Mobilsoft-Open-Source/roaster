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

import org.jboss.weld.environment.se.Weld;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

import com.codeborne.selenide.WebDriverRunner;

import hu.icellmobilsoft.roaster.weldunit.BaseWeldUnitType;

/**
 * Base class of Selenide TestCases, what is contained Selenide configuration
 *
 * @author speter555
 * @since 0.2.0
 */
public class BaseSelenideTestCase extends BaseWeldUnitType {

    @Override
    protected void configureWeld(Weld weld) {
        super.configureWeld(weld);
        weld.addExtensions(SelenideConfigExtension.class);
    }

    /**
     * Before each all test, setup webdriver if necessary
     */
    @BeforeEach
    public void setupWebDriver() {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        setupWebDriver(webDriver);
    }

    /**
     * Overridable method for configuring webDriver
     *
     * @param webDriver
     *            input webDriver instance for additional settings
     */
    protected void setupWebDriver(WebDriver webDriver) {
    }
}
