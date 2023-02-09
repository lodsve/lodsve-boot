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

parent_pom="./pom.xml"
old_version_number=`awk '/<revision>[^<]+<\/revision>/{gsub(/<revision>|<\/revision>/,"",$1);print $1;exit;}' $parent_pom`

old_version="<revision>$old_version_number<\/revision>"
new_version="<revision>$1<\/revision>"

os=$(uname -s)
if [[ $os == Darwin ]]; then
    sed -i "" "s/${old_version}/${new_version}/g" $parent_pom
else
    sed -i "s/${old_version}/${new_version}/g" $parent_pom
fi
