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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.codeborne.selenide.SelenideElement;

import hu.icellmobilsoft.roaster.selenide.AbstractBaseComponent;

/**
 * Angular Material radio group component ({@value TAG_NAME})
 * 
 * @author martin.nagy
 * @since 0.6.0
 */
public class MatRadioGroup extends AbstractBaseComponent {
    /**
     * Angular Material tag name
     */
    public static final String TAG_NAME = "mat-radio-group";

    /**
     * Initializes the object with the selenide element
     *
     * @param selenideElement
     *            the selenide element
     */
    public MatRadioGroup(SelenideElement selenideElement) {
        super(selenideElement);
    }

    /**
     * Selects an option with a given text
     * 
     * @param option
     *            the option to be selected
     */
    public void select(String option) {
        getSelenideElement().$x(".//*[@class='mat-radio-label-content' and normalize-space(text()) = '" + option + "']/..").click();
    }

    /**
     * Returns the text of the selected element. Returns {@link Optional#empty()} if no option is selected.
     * 
     * @return the text of the selected element
     */
    public Optional<String> getSelected() {
        SelenideElement selectedElement = getSelenideElement().$("mat-radio-button.mat-radio-checked .mat-radio-label-content");
        return toOptional(selectedElement).map(SelenideElement::getText);
    }

    /**
     * Returns the available options
     * 
     * @return the available options
     */
    public List<String> getOptions() {
        return getSelenideElement().$$("mat-radio-button .mat-radio-label-content").stream().map(SelenideElement::getText)
                .collect(Collectors.toList());
    }

}
