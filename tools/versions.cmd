@REM
@REM Licensed to the Apache Software Foundation (ASF) under one or more
@REM contributor license agreements.  See the NOTICE file distributed with
@REM this work for additional information regarding copyright ownership.
@REM The ASF licenses this file to You under the Apache License, Version 2.0
@REM (the "License"); you may not use this file except in compliance with
@REM the License.  You may obtain a copy of the License at
@REM
@REM      https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@echo off
setlocal enabledelayedexpansion
set "parent_pom=pom.xml"

for /f "tokens=3 delims=<>" %%a in ('type %parent_pom% ^| findstr /r "<revision>[^<]*</revision>"') do (
  set "old_version_number=%%a"
)

set "old_version=<revision>!old_version_number!</revision>"
set "new_version=<revision>%1%</revision>"

set os=%OS%
if "%os%"=="Windows_NT" (
    powershell -Command "(Get-Content %parent_pom%) -replace '%old_version%', '%new_version%' | Set-Content %parent_pom%"
) else (
    echo Unsupported OS
)