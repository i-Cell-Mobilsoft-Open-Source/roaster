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
/*
 * License: Apache License, Version 2.0
 * See the LICENSE file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package hu.icellmobilsoft.roaster.testsuite.hibernate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import hu.icellmobilsoft.coffee.model.base.javatime.AbstractIdentifiedAuditEntity;

/**
 * Test entity.
 *
 * @author attila-kiss-it
 * @since 2.2.0
 */
@Entity
@Table(name = "MY_TABLE")
public class MyTable extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

}
