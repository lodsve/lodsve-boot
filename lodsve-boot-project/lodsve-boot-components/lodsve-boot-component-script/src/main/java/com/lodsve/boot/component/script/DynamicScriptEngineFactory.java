/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lodsve.boot.component.script;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据语言类型获取编译引擎的工厂.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class DynamicScriptEngineFactory {
    private static final Map<String, ScriptEngine> SCRIPT_ENGINE = new HashMap<>();

    public DynamicScriptEngineFactory(List<ScriptEngine> engines) {
        resolveEngine(engines);
    }

    /**
     * 根据语言类型获取编译引擎
     *
     * @param scriptName 语言类型
     * @return 编译引擎
     */
    public static ScriptEngine getEngine(String scriptName) {
        Assert.notNull(scriptName, "类型不可为空!");

        ScriptEngine engine = SCRIPT_ENGINE.get(scriptName);
        if (null == engine) {
            throw new UnsupportedOperationException(scriptName + " is not supported for Dynamic Script Language!");
        }

        return engine;
    }

    private void resolveEngine(List<ScriptEngine> engines) {
        engines.forEach(e -> {
            String scriptName = e.type();
            if (!SCRIPT_ENGINE.containsKey(scriptName)) {
                SCRIPT_ENGINE.put(scriptName, e);
            }
        });
    }
}
