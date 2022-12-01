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
import java.util.stream.Collectors;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import hu.icellmobilsoft.roaster.selenide.AbstractBaseComponent;

/**
 * Angular Material select component ({@value TAG_NAME})
 * 
 * @author martin.nagy
 * @since 0.6.0
 */
public class MatSelect extends AbstractBaseComponent {
    /**
     * Angular Material tag name
     */
    public static final String TAG_NAME = "mat-select";
    private final SelenideElement body = Selenide.$("body");

    /**
     * Initializes the object with the selenide element
     *
     * @param selenideElement
     *            the selenide element
     */
    public MatSelect(SelenideElement selenideElement) {
        super(selenideElement);
    }

    /**
     * Selects an options with a given text. Clears all previously selected options.
     *
     * @param options
     *            the options to be selected
     */
    public void select(String... options) {
        openOverlay();
        SelenideElement selectPanel = getSelectPanel();
        deselectAll(selectPanel);
        for (String option : options) {
            selectPanel.$x(".//mat-option/*[@class='mat-option-text' and normalize-space(text()) = '" + option + "']/..").click();
        }
        closeOverlay();
    }

    /**
     * Returns the list of the selected elements.
     *
     * @return the list of the selected elements
     */
    public List<String> getSelected() {
        openOverlay();
        List<String> selected = getSelectPanel().$$("mat-option.mat-selected .mat-option-text").stream().map(SelenideElement::getText)
                .collect(Collectors.toList());
        closeOverlay();
        return selected;
    }

    /**
     * Returns the available options
     *
     * @return the available options
     */
    public List<String> getOptions() {
        openOverlay();
        List<String> options = getSelectPanel().$$("mat-option .mat-option-text").stream().map(SelenideElement::getText).collect(Collectors.toList());
        closeOverlay();
        return options;
    }

    /**
     * Returns the overlay containing the selectable options
     * 
     * @return the overlay containing the selectable options
     */
    protected SelenideElement getSelectPanel() {
        return Selenide.$(".mat-select-panel").shouldBe(Condition.visible);
    }

    /**
     * Deselects all previously selected options
     * 
     * @param selectPanel
     *            the overlay containing the selectable options
     */
    protected void deselectAll(SelenideElement selectPanel) {
        for (SelenideElement element : selectPanel.$$("mat-option.mat-selected.mat-option-multiple")) {
            element.click();
        }
    }

    /**
     * Opens the overlay containing the selectable options
     */
    protected void openOverlay() {
        getSelenideElement().click();
    }

    /**
     * Closes the overlay containing the selectable options
     */
    protected void closeOverlay() {
        body.click();
    }
}
