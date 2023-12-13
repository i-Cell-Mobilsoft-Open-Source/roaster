/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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

import jakarta.enterprise.inject.Vetoed;

import org.openqa.selenium.JavascriptExecutor;

import com.codeborne.selenide.WebDriverRunner;

/**
 * Selenide utils
 *
 * @author gyorgy.krnyan
 * @since 2.2.0
 */
@Vetoed
public class SelenideUtil {
    protected SelenideUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Gets content of clipboard (with chrome)
     *
     * @return the content of clipboard
     */
    public static String getContentOfClipboard() {
        JavascriptExecutor js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
        js.executeScript(
                "async function getCBContents() { try { window.cb = await navigator.clipboard.readText(); console.log(\"Pasted content: \", window.cb); } catch (err) { console.error(\"Failed to read clipboard contents: \", err); window.cb = \"Error : \" + err; } } getCBContents();");
        return js.executeScript("return window.cb;").toString();
    }
}
