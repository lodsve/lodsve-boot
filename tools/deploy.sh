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

profile=$1
if [[ $profile == release-lodsve ]]; then
  # skip test
  ./mvnw clean deploy -P $1 -Dmaven.test.skip=true
else
  if [ -z "$2" ]; then
    echo "The password for the gpg key to publish the maven repository cannot be empty."
    exit 1
  fi
  ./mvnw clean deploy -P $1 -Dgpg.passphrase=$2 -Dmaven.test.skip=false
fi

