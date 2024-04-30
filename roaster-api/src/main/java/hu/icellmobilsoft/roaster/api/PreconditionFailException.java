/*-
 * #%L
 * Roaster
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
package hu.icellmobilsoft.roaster.api;

/**
 * This exception should be thrown if the precondition check fails.
 *
 */
public class PreconditionFailException extends TestException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message
     * 
     * @see RuntimeException#RuntimeException(String)
     * @param message
     *            the detail message.
     */
    public PreconditionFailException(final String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * 
     * @see RuntimeException#RuntimeException(String,Throwable)
     * @param message
     *            the detail message.
     * @param cause
     *            the cause.
     */
    public PreconditionFailException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause
     * 
     * @see RuntimeException#RuntimeException(Throwable)
     * @param cause
     *            the cause.
     */
    public PreconditionFailException(final Throwable cause) {
        super(cause);
    }

}
