/*====================================================================================
    Copyright 2015 Mahisoft Inc. (Yessika Labrador, Federico Ponte and Joaquin Delgado)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
======================================================================================*/

package com.mahisoft.elasticsearchprediction.plugin.domain;

public class IndexValue {

    private IndexAttributeDefinition definition;

    private Object value;

    public IndexValue(IndexAttributeDefinition definition, Object value) {
        this.definition = definition;
        this.value = value;
    }

    public IndexAttributeDefinition getDefinition() {
        return definition;
    }

    public Object getValue() {
        return value;
    }

}
