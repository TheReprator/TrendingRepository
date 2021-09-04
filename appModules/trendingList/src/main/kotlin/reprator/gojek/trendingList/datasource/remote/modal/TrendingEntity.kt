/*
 * Copyright 2021 Vikram LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reprator.gojek.trendingList.datasource.remote.modal

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    "author",
    "name",
    "avatar",
    "url",
    "description",
    "language",
    "languageColor",
    "stars",
    "forks"
)
class TrendingEntity(
    @JsonProperty("author")
    val author: String ?,
    @JsonProperty("name")
    val name: String ?,
    @JsonProperty("avatar")
    val avatar: String ?,
    @JsonProperty("url")
    val url: String ?,
    @JsonProperty("description")
    val description: String ?,
    @JsonProperty("language")
    val language: String ?,
    @JsonProperty("languageColor")
    val languageColor: String ?,
    @JsonProperty("stars")
    val stars: Int ?,
    @JsonProperty("forks")
    val forks: Int ?
)
