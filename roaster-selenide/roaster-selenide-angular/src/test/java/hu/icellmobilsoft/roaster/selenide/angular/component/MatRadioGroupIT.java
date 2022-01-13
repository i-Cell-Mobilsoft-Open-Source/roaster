/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.roaster.selenide.angular.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.codeborne.selenide.Selenide;

import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.selenide.BaseSelenideTestCase;

/**
 * Selenide test for {@link MatRadioGroup}
 * 
 * @author martin.nagy
 * @since 0.6.0
 */
@Tag(TestSuiteGroup.SELENIDE)
class MatRadioGroupIT extends BaseSelenideTestCase {

    @Test
    void test() {
        Selenide.open("/components/radio/overview");

        MatRadioGroup radioGroup = new MatRadioGroup(Selenide.$("#radio-overview " + MatRadioGroup.TAG_NAME));
        radioGroup.getSelenideElement().scrollIntoView(true);
        assertTrue(radioGroup.getSelected().isEmpty());
        radioGroup.select("Option 2");
        assertEquals("Option 2", radioGroup.getSelected().orElseThrow());
        List<String> options = radioGroup.getOptions();
        assertIterableEquals(List.of("Option 1", "Option 2"), options);
        System.out.println("Options: " + options);
    }
}
