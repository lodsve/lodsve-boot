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
cls

call tools\logo.cmd

if "%1" == "versions" (
  call tools\versions.cmd %2
) else if "%1" == "deployOss" (
  call tools\deploy.cmd release-oss %2
) else if "%1" == "deployLodsve" (
  call tools\deploy.cmd release-lodsve %2
) else (
  echo --------------------------------------------------------------------------
  echo.
  echo usage: lodsve.cmd [versions ^| deployOss ^| deployLodsve]
  echo.
  echo versions          Update lodsve-boot versions.
  echo deployOss         Deploy lodsve-boot to maven repository.
  echo deployLodsve      Deploy lodsve-boot to Lodsve repository.
  echo.
  echo --------------------------------------------------------------------------
)
