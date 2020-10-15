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
package com.lodsve.boot.autoconfigure.script;

import com.lodsve.boot.script.*;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
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
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
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
