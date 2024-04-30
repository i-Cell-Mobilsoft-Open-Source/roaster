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

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import hu.icellmobilsoft.roaster.selenide.AbstractBaseComponent;

/**
 * Angular Material checkbox component ({@value TAG_NAME})
 * 
 * @author martin.nagy
 * @since 0.6.0
 */
public class MatCheckbox extends AbstractBaseComponent {
    /**
     * Angular Material tag name
     */
    public static final String TAG_NAME = "mat-checkbox";

    /**
     * Initializes the object with the selenide element
     *
     * @param selenideElement
     *            the selenide element
     */
    public MatCheckbox(SelenideElement selenideElement) {
        super(selenideElement);
    }

    /**
     * Toggles the value of the component
     */
    public void toggle() {
        getSelenideElement().click();
    }

    /**
     * Returns the value of the component. {@code true} if checked, {@code false} if unchecked, and {@code null} if the value is indeterminate.
     * 
     * @return the value of the component
     */
    public Boolean getValue() {
        return isIndeterminate() ? null : isChecked();
    }

    /**
     * Returns {@code true} if the element is checked
     * 
     * @return {@code true} if the element is checked
     */
    public boolean isChecked() {
        return getSelenideElement().is(Condition.cssClass("mat-checkbox-checked"));
    }

    /**
     * Returns {@code true} if the value is indeterminate
     * 
     * @return {@code true} if the value is indeterminate
     */
    public boolean isIndeterminate() {
        return getSelenideElement().is(Condition.cssClass("mat-checkbox-indeterminate"));
    }

}
