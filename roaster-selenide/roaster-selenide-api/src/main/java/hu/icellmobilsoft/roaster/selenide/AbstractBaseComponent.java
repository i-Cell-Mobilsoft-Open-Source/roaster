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
package hu.icellmobilsoft.roaster.selenide;

import java.util.Optional;

import com.codeborne.selenide.SelenideElement;

/**
 * Base class for complex selenide components
 * 
 * @author martin.nagy
 * @since 0.6.0
 */
public abstract class AbstractBaseComponent {
    private final SelenideElement selenideElement;

    /**
     * Initializes the object with the selenide element
     * 
     * @param selenideElement
     *            the selenide element
     */
    protected AbstractBaseComponent(SelenideElement selenideElement) {
        this.selenideElement = selenideElement;
    }

    /**
     * Getter for the field {@code selenideElement}.
     *
     * @return selenideElement
     */
    public SelenideElement getSelenideElement() {
        return selenideElement;
    }

    /**
     * Checks if the passed element exists and returns an {@code Optional} based on that
     * 
     * @param element
     *            the element to check
     * @return the element wrapped in {@code Optional}
     */
    protected Optional<SelenideElement> toOptional(SelenideElement element) {
        return Optional.of(element).filter(SelenideElement::exists);
    }
}
