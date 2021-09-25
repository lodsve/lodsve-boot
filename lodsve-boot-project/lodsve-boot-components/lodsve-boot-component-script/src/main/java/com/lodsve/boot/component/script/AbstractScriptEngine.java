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

import com.lodsve.boot.utils.EncryptUtils;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 其实就是使用jdk自带的脚本引擎.
 *
 * @author Hulk Sun
 */
public abstract class AbstractScriptEngine implements com.lodsve.boot.component.script.ScriptEngine {
    /**
     * 编译后上下文的缓存
     */
    private static final Map<String, com.lodsve.boot.component.script.ScriptContext> SCRIPT_CONTENT_CACHE = new HashMap<>();
    /**
     * 编译器
     */
    private Compilable compilable;
    /**
     * 未初始化的参数绑定
     */
    private Bindings unInitBindings;

    public AbstractScriptEngine() {
        try {
            init();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(Object... args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        javax.script.ScriptEngine engine = manager.getEngineByName(type());
        compilable = (Compilable) engine;
        unInitBindings = engine.createBindings();
        CompiledScript script;
        for (Object arg : args) {
            script = compilable.compile(arg.toString());
            script.eval(unInitBindings);
        }
        SCRIPT_CONTENT_CACHE.clear();
    }

    @Override
    public boolean compile(String id, String scriptText) throws ScriptException {
        if (compilable == null) {
            init();
        }

        com.lodsve.boot.component.script.ScriptContext context = SCRIPT_CONTENT_CACHE.get(id);
        if (context != null && EncryptUtils.encodeMd5(scriptText).equals(context.getMd5())) {
            return context.getScript() != null;
        }

        CompiledScript compiledScript = compilable.compile(scriptText);
        SCRIPT_CONTENT_CACHE.put(id, new com.lodsve.boot.component.script.ScriptContext(id, EncryptUtils.encodeMd5(scriptText), compiledScript));

        return compiledScript != null;
    }

    @Override
    public boolean isCompiled(String id) {
        return SCRIPT_CONTENT_CACHE.containsKey(id);
    }

    @Override
    public boolean remove(String id) {
        return SCRIPT_CONTENT_CACHE.remove(id) != null;
    }

    @Override
    public ScriptResult execute(String id, Map<String, Object> args) {
        long startTime = System.currentTimeMillis();
        com.lodsve.boot.component.script.ScriptContext script = SCRIPT_CONTENT_CACHE.get(id);

        if (script == null || script.getScript() == null) {
            return ScriptResult.failure(String.format("script(%s): %s not found!", type(), id), null, System.currentTimeMillis() - startTime);
        }

        Object result = null;
        boolean success = false;
        String message = "";
        Exception exception = null;
        try {
            javax.script.ScriptContext context = new SimpleScriptContext();
            context.setBindings(unInitBindings, javax.script.ScriptContext.GLOBAL_SCOPE);
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                context.setAttribute(entry.getKey(), entry.getValue(), javax.script.ScriptContext.ENGINE_SCOPE);
            }

            result = script.getScript().eval(context);
            success = true;
        } catch (ScriptException e) {
            exception = e;
        }

        long useTime = System.currentTimeMillis() - startTime;
        if (success) {
            return ScriptResult.success(result, useTime);
        } else {
            return ScriptResult.failure(message, exception, useTime);
        }
    }

    @Override
    public ScriptResult execute(String id) {
        return execute(id, Collections.emptyMap());
    }

    @Override
    public ScriptResult invoke(String id, String method, Object... args) throws ScriptException {
        long startTime = System.currentTimeMillis();
        com.lodsve.boot.component.script.ScriptContext script = SCRIPT_CONTENT_CACHE.get(id);

        if (script == null || script.getScript() == null) {
            return ScriptResult.failure(String.format("script(%s): %s not found!", type(), id), null, System.currentTimeMillis() - startTime);
        }

        Bindings bindings = script.getScript().getEngine().getBindings(javax.script.ScriptContext.ENGINE_SCOPE);
        script.getScript().eval(bindings);

        Invocable invocable = (Invocable) script.getScript().getEngine();

        boolean result = false;
        Object value = null;
        String message = "";
        Exception exception = null;
        try {
            value = invocable.invokeFunction(method, args);
            result = true;
        } catch (NoSuchMethodException e) {
            exception = e;
        }

        long useTime = System.currentTimeMillis() - startTime;
        if (result) {
            return ScriptResult.success(value, useTime);
        } else {
            return ScriptResult.failure(message, exception, useTime);
        }
    }
}
