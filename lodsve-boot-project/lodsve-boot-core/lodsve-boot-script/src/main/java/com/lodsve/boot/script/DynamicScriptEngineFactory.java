/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.lodsve.boot.script;

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
