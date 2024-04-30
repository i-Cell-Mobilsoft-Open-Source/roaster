/*-
 * #%L
 * Roaster
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.codeborne.selenide.Selenide;

import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.selenide.BaseSelenideTestCase;

/**
 * Selenide test for {@link MatCheckbox}
 * 
 * @author martin.nagy
 * @since 0.6.0
 */
@Tag(TestSuiteGroup.SELENIDE)
class MatCheckboxIT extends BaseSelenideTestCase {

    @Test
    void test() {
        Selenide.open("/components/checkbox/overview");

        MatCheckbox checkbox = new MatCheckbox(Selenide.$(".example-section " + MatCheckbox.TAG_NAME));
        checkbox.getSelenideElement().scrollIntoView(true);
        assertFalse(checkbox.getValue());
        checkbox.toggle();
        assertTrue(checkbox.getValue());
    }

}
