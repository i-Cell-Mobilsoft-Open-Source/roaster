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

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.codeborne.selenide.Selenide;

import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.selenide.BaseSelenideTestCase;

/**
 * Selenide test for {@link MatSelect}
 * 
 * @author martin.nagy
 * @since 0.6.0
 */
@Tag(TestSuiteGroup.SELENIDE)
class MatSelectIT extends BaseSelenideTestCase {

    @Test
    void test() {
        Selenide.open("/components/select/overview");

        // single select
        MatSelect singleSelect = new MatSelect(Selenide.$("#select-overview " + MatSelect.TAG_NAME));
        singleSelect.getSelenideElement().scrollIntoView(true);
        singleSelect.select("Pizza");
        assertIterableEquals(List.of("Pizza"), singleSelect.getSelected());
        List<String> options = singleSelect.getOptions();
        assertEquals(3, options.size());
        System.out.println("Options: " + options);

        // multi select
        MatSelect multiSelect = new MatSelect(Selenide.$("#select-multiple " + MatSelect.TAG_NAME));
        multiSelect.getSelenideElement().scrollIntoView(true);
        multiSelect.select("Onion", "Pepperoni");
        assertIterableEquals(List.of("Onion", "Pepperoni"), multiSelect.getSelected());
        options = multiSelect.getOptions();
        assertEquals(6, options.size());
        System.out.println("Options: " + options);

        multiSelect.select("Tomato");
        assertIterableEquals(List.of("Tomato"), multiSelect.getSelected());
    }
}
