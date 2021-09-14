#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


mvn versions:set -DnewVersion=$1
mvn versions:commit

dependencies_pom=lodsve-boot-project/lodsve-boot-dependencies/pom.xml
old_version_number=`awk '/<lodsve.boot.version>[^<]+<\/lodsve.boot.version>/{gsub(/<lodsve.boot.version>|<\/lodsve.boot.version>/,"",$1);print $1;exit;}' $dependencies_pom`

old_version="<lodsve.boot.version>$old_version_number<\/lodsve.boot.version>"
new_version="<lodsve.boot.version>$1<\/lodsve.boot.version>"

os=$(uname -s)
if [[ $os == Darwin ]]; then
    sed -i "" "s/${old_version}/${new_version}/g" $dependencies_pom
else
    sed -i "s/${old_version}/${new_version}/g" $dependencies_pom
fi
