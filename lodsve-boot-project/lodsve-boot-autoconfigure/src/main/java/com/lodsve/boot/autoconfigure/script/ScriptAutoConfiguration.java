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
package com.lodsve.boot.autoconfigure.script;

import com.lodsve.boot.component.script.DynamicScriptEngineFactory;
import com.lodsve.boot.component.script.GroovyScriptEngine;
import com.lodsve.boot.component.script.JsScriptEngine;
import com.lodsve.boot.component.script.PythonScriptEngine;
import com.lodsve.boot.component.script.RubyScriptEngine;
import com.lodsve.boot.component.script.ScriptEngine;
import com.lodsve.boot.component.script.SpelScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;
import org.jruby.embed.jsr223.JRubyEngineFactory;
import org.python.jsr223.PyScriptEngineFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ExpressionParser;

import java.util.List;

/**
 * 异构语言支持.
 *
 * @author Hulk Sun
 */
@ConditionalOnClass(DynamicScriptEngineFactory.class)
@Configuration
public class ScriptAutoConfiguration {
    @Bean
    public DynamicScriptEngineFactory engineFactory(List<ScriptEngine> engines) {
        return new DynamicScriptEngineFactory(engines);
    }

    @Configuration
    @ConditionalOnClass(NashornScriptEngineFactory.class)
    static class JavaScriptEngineConfiguration {
        @Bean
        public ScriptEngine javascript() {
            return new JsScriptEngine();
        }
    }

    @Configuration
    @ConditionalOnClass(PyScriptEngineFactory.class)
    static class PythonEngineConfiguration {
        @Bean
        public ScriptEngine python() {
            return new PythonScriptEngine();
        }
    }

    @Configuration
    @ConditionalOnClass(GroovyScriptEngineFactory.class)
    static class GroovyEngineConfiguration {
        @Bean
        public ScriptEngine groovy() {
            return new GroovyScriptEngine();
        }
    }

    @Configuration
    @ConditionalOnClass(JRubyEngineFactory.class)
    static class RubyEngineConfiguration {
        @Bean
        public ScriptEngine ruby() {
            return new RubyScriptEngine();
        }
    }

    @Configuration
    @ConditionalOnClass(ExpressionParser.class)
    static class SpelEngineConfiguration {
        @Bean
        public ScriptEngine spel() {
            return new SpelScriptEngine();
        }
    }
}
