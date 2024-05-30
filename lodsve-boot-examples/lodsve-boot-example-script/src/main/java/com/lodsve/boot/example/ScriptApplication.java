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
package com.lodsve.boot.example;

import com.google.common.collect.Lists;
import com.lodsve.boot.LodsveBootApplication;
import com.lodsve.boot.component.script.DynamicScriptEngineFactory;
import com.lodsve.boot.component.script.ScriptEngine;
import com.lodsve.boot.component.script.ScriptResult;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptException;
import java.util.List;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@SpringBootApplication
@RestController
public class ScriptApplication {
    public static void main(String[] args) {
        LodsveBootApplication.run(ScriptApplication.class, args);
    }

    @GetMapping("/js")
    public List<ScriptResult> test1() throws ScriptException {
        String jsCode1 = "parseInt(1+2)";
        String jsCode2 = "function testJs(arg1,arg2) {return parseInt(arg1+arg2);}";

        ScriptEngine js = DynamicScriptEngineFactory.getEngine("javascript");
        boolean compileJsCode1 = js.compile("jsCode1", jsCode1);
        boolean compileJsCode2 = js.compile("jsCode2", jsCode2);
        System.out.println("compileJsCode1=" + compileJsCode1);
        System.out.println("compileJsCode2=" + compileJsCode2);

        ScriptResult r1 = js.execute("jsCode1");
        ScriptResult r2 = js.invoke("jsCode2", "testJs", 1, 2);

        return Lists.newArrayList(r1, r2);
    }

    @GetMapping("/groovy")
    public List<ScriptResult> test2() throws ScriptException {
        String groovyCode1 = "1+2";
        String groovyCode2 = "String testGroovy(arg1,arg2) {return arg1+arg2;}";

        ScriptEngine groovy = DynamicScriptEngineFactory.getEngine("groovy");
        boolean compileGroovyCode1 = groovy.compile("groovyCode1", groovyCode1);
        boolean compileGroovyCode2 = groovy.compile("groovyCode2", groovyCode2);

        System.out.println("compileGroovyCode1=" + compileGroovyCode1);
        System.out.println("compileGroovyCode2=" + compileGroovyCode2);

        ScriptResult r1 = groovy.execute("groovyCode1");
        ScriptResult r2 = groovy.invoke("groovyCode2", "testGroovy", 1, 2);

        return Lists.newArrayList(r1, r2);
    }

    @GetMapping("/python")
    public List<ScriptResult> test3() throws ScriptException {
        String pythonCode1 = "1+2";
        String pythonCode2 = "def testPython( str ):\n\treturn str + \" World!\";";

        ScriptEngine python = DynamicScriptEngineFactory.getEngine("python");
        boolean compilePythonCode1 = python.compile("pythonCode1", pythonCode1);
        boolean compilePythonCode2 = python.compile("pythonCode2", pythonCode2);

        System.out.println("compilePythonCode1=" + compilePythonCode1);
        System.out.println("compilePythonCode2=" + compilePythonCode2);

        ScriptResult r1 = python.execute("pythonCode1");
        ScriptResult r2 = python.invoke("pythonCode2", "testPython", "Hello");

        return Lists.newArrayList(r1, r2);
    }

    @GetMapping("/ruby")
    public List<ScriptResult> test4() throws ScriptException {
        String rubyCode1 = "2+19";
        String rubyCode2 = "def testRuby (arg1, arg2)\n\tresult = arg1 + arg2\nreturn result\nend";

        ScriptEngine ruby = DynamicScriptEngineFactory.getEngine("ruby");
        boolean compileRubyCode1 = ruby.compile("rubyCode1", rubyCode1);
        boolean compileRubyCode2 = ruby.compile("rubyCode2", rubyCode2);

        System.out.println("compileRubyCode1=" + compileRubyCode1);
        System.out.println("compileRubyCode2=" + compileRubyCode2);

        ScriptResult r1 = ruby.execute("rubyCode1");
        ScriptResult r2 = ruby.invoke("rubyCode2", "testRuby", 1, 2);

        return Lists.newArrayList(r1, r2);
    }

    @GetMapping("/spel")
    public List<ScriptResult> test5() throws ScriptException {
        String spelCode1 = "2+3";

        ScriptEngine spel = DynamicScriptEngineFactory.getEngine("spel");
        boolean compileSpelCode = spel.compile("spelCode1", spelCode1);

        System.out.println("compileSpelCode=" + compileSpelCode);

        ScriptResult r1 = spel.execute("spelCode1");

        return Lists.newArrayList(r1);
    }
}
