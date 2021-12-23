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
package hu.icellmobilsoft.roaster.selenide.api.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.selenide.BaseSelenideTestCase;

/**
 * Selenide teszt, where call https://google.com and write into search the 'icellmobilsoft' word, and send enter for searching, and it is looking for
 * https://icellmobilsoft.hu/hu/ result.
 *
 * @author speter555
 * @since 0.2.0
 */
@Tag(TestSuiteGroup.SELENIDE)
@Disabled("Only for testing!!!")
public class GoogleIcellMobilsoftTest extends BaseSelenideTestCase {

    @Test
    @DisplayName("Search 'icellmobilsoft' word on Google.com")
    @Disabled("Only for testing!!!")
    public void testGoogleWithIcellmobilsoftSearch() {
        // You don't have to use Selenide.open(<url>), it come from configuration, and do it automatically
        SelenideElement qInput = Selenide.$("input[name='q']");
        qInput.setValue("icellmobilsoft").pressEnter();

        SelenideElement result = Selenide.$("div#search");
        ElementsCollection resultList = result.$$("div.g a[href='https://icellmobilsoft.hu/hu/']");
        resultList.first().shouldHave(Condition.visible);
    }
}
